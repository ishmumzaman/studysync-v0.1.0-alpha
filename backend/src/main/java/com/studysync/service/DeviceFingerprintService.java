package com.studysync.service;

import com.studysync.dto.auth.DeviceInfo;
import com.studysync.model.User;
import com.studysync.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceFingerprintService {

    private final UserRepository userRepository;

    public DeviceFingerprint generateFingerprint(HttpServletRequest request) {
        return DeviceFingerprint.builder()
            .userAgent(request.getHeader("User-Agent"))
            .acceptLanguage(request.getHeader("Accept-Language"))
            .screenResolution(request.getHeader("X-Screen-Resolution"))
            .timezone(request.getHeader("X-Timezone"))
            .platform(request.getHeader("X-Platform"))
            .appVersion(request.getHeader("X-App-Version"))
            .deviceModel(request.getHeader("X-Device-Model"))
            .ipAddress(getClientIpAddress(request))
            .fingerprint(calculateFingerprint(request))
            .build();
    }

    public boolean validateDevice(String userId, DeviceFingerprint fingerprint) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        List<User.DeviceFingerprint> knownDevices = user.getDeviceFingerprints();

        return knownDevices.stream()
            .anyMatch(known -> calculateSimilarity(known, fingerprint) > 0.8);
    }

    private double calculateSimilarity(User.DeviceFingerprint known, DeviceFingerprint current) {
        double platformMatch = 0.0;
        if (known.getDeviceInfo() != null && current.getPlatform() != null) {
            platformMatch = known.getDeviceInfo().getPlatform().equals(current.getPlatform()) ? 1.0 : 0.0;
        }
        
        // Simple similarity calculation - can be enhanced with more sophisticated algorithms
        return platformMatch;
    }

    private String calculateFingerprint(HttpServletRequest request) {
        try {
            String data = request.getHeader("User-Agent") + 
                         request.getHeader("Accept-Language") +
                         request.getHeader("X-Platform") +
                         request.getHeader("X-Device-Model");
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error calculating fingerprint", e);
            return "unknown";
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DeviceFingerprint {
        private String userAgent;
        private String acceptLanguage;
        private String screenResolution;
        private String timezone;
        private String platform;
        private String appVersion;
        private String deviceModel;
        private String ipAddress;
        private String fingerprint;
    }
}



