package com.studysync.service;

import com.studysync.dto.auth.*;
import com.studysync.exception.EmailAlreadyExistsException;
import com.studysync.exception.InvalidCredentialsException;
import com.studysync.model.User;
import com.studysync.repository.UserRepository;
import com.studysync.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final DeviceFingerprintService deviceFingerprintService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        // Create new user
        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .displayName(request.getDisplayName())
            .timezone(request.getTimezone())
            .groups(new ArrayList<>())
            .friends(new ArrayList<>())
            .deviceFingerprints(new ArrayList<>())
            .securityEvents(new ArrayList<>())
            .build();

        // Add device fingerprint if provided
        if (request.getDeviceInfo() != null) {
            User.DeviceFingerprint fingerprint = createDeviceFingerprint(request.getDeviceInfo());
            user.getDeviceFingerprints().add(fingerprint);
        }

        // Save user
        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // Generate tokens
        JwtTokenProvider.TokenPair tokens = jwtTokenProvider.generateTokenPair(
            user.getId(),
            request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceId() : "unknown"
        );

        // Record security event
        recordSecurityEvent(user.getId(), "REGISTRATION", true, request.getDeviceInfo());

        return AuthResponse.builder()
            .user(mapToUserDto(user))
            .tokens(tokens)
            .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            // Get user from database
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

            // Update device fingerprint
            if (request.getDeviceInfo() != null) {
                updateDeviceFingerprint(user, request.getDeviceInfo());
            }

            // Generate tokens
            JwtTokenProvider.TokenPair tokens = jwtTokenProvider.generateTokenPair(
                user.getId(),
                request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceId() : "unknown"
            );

            // Update last activity
            user.getAnalytics().setLastActivityDate(Instant.now());
            userRepository.save(user);

            // Record security event
            recordSecurityEvent(user.getId(), "LOGIN", true, request.getDeviceInfo());

            log.info("User logged in: {}", user.getEmail());

            return AuthResponse.builder()
                .user(mapToUserDto(user))
                .tokens(tokens)
                .build();

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            recordSecurityEvent(null, "LOGIN_FAILED", false, request.getDeviceInfo());
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    public JwtTokenProvider.TokenPair refreshToken(String refreshToken) {
        return jwtTokenProvider.refreshTokens(refreshToken);
    }

    public void logout(String refreshToken) {
        jwtTokenProvider.invalidateRefreshToken(refreshToken);
        log.info("User logged out, refresh token invalidated");
    }

    private User.DeviceFingerprint createDeviceFingerprint(DeviceInfo deviceInfo) {
        return User.DeviceFingerprint.builder()
            .deviceId(deviceInfo.getDeviceId())
            .lastSeen(Instant.now())
            .deviceInfo(User.DeviceInfo.builder()
                .platform(deviceInfo.getPlatform())
                .version(deviceInfo.getVersion())
                .model(deviceInfo.getModel())
                .build())
            .isActive(true)
            .build();
    }

    private void updateDeviceFingerprint(User user, DeviceInfo deviceInfo) {
        boolean found = false;
        for (User.DeviceFingerprint fingerprint : user.getDeviceFingerprints()) {
            if (fingerprint.getDeviceId().equals(deviceInfo.getDeviceId())) {
                fingerprint.setLastSeen(Instant.now());
                fingerprint.setActive(true);
                found = true;
                break;
            }
        }
        
        if (!found) {
            user.getDeviceFingerprints().add(createDeviceFingerprint(deviceInfo));
        }
    }

    private void recordSecurityEvent(String userId, String type, boolean success, DeviceInfo deviceInfo) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                User.SecurityEvent event = User.SecurityEvent.builder()
                    .type(type)
                    .timestamp(Instant.now())
                    .deviceId(deviceInfo != null ? deviceInfo.getDeviceId() : "unknown")
                    .ipAddress(deviceInfo != null ? deviceInfo.getIpAddress() : "unknown")
                    .success(success)
                    .build();
                
                user.getSecurityEvents().add(event);
                
                // Keep only last 100 security events
                if (user.getSecurityEvents().size() > 100) {
                    user.setSecurityEvents(
                        user.getSecurityEvents().subList(
                            user.getSecurityEvents().size() - 100,
                            user.getSecurityEvents().size()
                        )
                    );
                }
                
                userRepository.save(user);
            });
        }
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .avatarUrl(user.getAvatarUrl())
            .timezone(user.getTimezone())
            .preferences(user.getPreferences())
            .analytics(user.getAnalytics())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}



