package com.studysync.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${spring.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RedisTemplate<String, Object> redisTemplate;

    public TokenPair generateTokenPair(String userId, String deviceId) {
        String accessToken = createAccessToken(userId, deviceId);
        String refreshToken = createRefreshToken(userId, deviceId);

        // Store refresh token in Redis
        String refreshTokenKey = "refresh_token:" + refreshToken;
        RefreshTokenData tokenData = RefreshTokenData.builder()
            .userId(userId)
            .deviceId(deviceId)
            .createdAt(new Date())
            .build();
        
        redisTemplate.opsForValue().set(
            refreshTokenKey,
            tokenData,
            Duration.ofMillis(refreshTokenExpiration)
        );

        return TokenPair.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(accessTokenExpiration / 1000)
            .build();
    }

    public TokenPair refreshTokens(String refreshToken) {
        String refreshTokenKey = "refresh_token:" + refreshToken;
        RefreshTokenData tokenData = (RefreshTokenData) redisTemplate.opsForValue().get(refreshTokenKey);
        
        if (tokenData == null) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Check if token is blacklisted
        if (isTokenBlacklisted(refreshToken)) {
            throw new InvalidTokenException("Token has been revoked");
        }

        // Rotate refresh token for security
        invalidateRefreshToken(refreshToken);
        return generateTokenPair(tokenData.getUserId(), tokenData.getDeviceId());
    }

    private String createAccessToken(String userId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("device_id", deviceId);
        claims.put("token_type", "access");
        claims.put("issued_at", System.currentTimeMillis());
        
        return createToken(claims, userId, accessTokenExpiration);
    }

    private String createRefreshToken(String userId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("device_id", deviceId);
        claims.put("token_type", "refresh");
        claims.put("token_id", UUID.randomUUID().toString());
        
        return createToken(claims, userId, refreshTokenExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractDeviceId(String token) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get("device_id");
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation error: ", e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void invalidateRefreshToken(String refreshToken) {
        String refreshTokenKey = "refresh_token:" + refreshToken;
        redisTemplate.delete(refreshTokenKey);
        
        // Add to blacklist
        String blacklistKey = "blacklist:" + refreshToken;
        redisTemplate.opsForValue().set(blacklistKey, true, Duration.ofDays(30));
    }

    private boolean isTokenBlacklisted(String token) {
        String blacklistKey = "blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TokenPair {
        private String accessToken;
        private String refreshToken;
        private long expiresIn;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class RefreshTokenData {
        private String userId;
        private String deviceId;
        private Date createdAt;
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}



