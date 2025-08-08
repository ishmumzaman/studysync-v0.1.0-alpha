package com.studysync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String passwordHash;
    
    @TextIndexed
    private String displayName;
    
    private String avatarUrl;
    
    private String timezone;
    
    @Builder.Default
    private ProfileVisibility profileVisibility = ProfileVisibility.builder().build();
    
    @Builder.Default
    private UserPreferences preferences = UserPreferences.builder().build();
    
    @Builder.Default
    private List<String> groups = new ArrayList<>();
    
    @Builder.Default
    private List<String> friends = new ArrayList<>();
    
    @Builder.Default
    private List<DeviceFingerprint> deviceFingerprints = new ArrayList<>();
    
    @Builder.Default
    private List<SecurityEvent> securityEvents = new ArrayList<>();
    
    @Builder.Default
    private UserAnalytics analytics = UserAnalytics.builder().build();
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @Version
    private Long version;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileVisibility {
        @Builder.Default
        private String studyTime = "friends";
        @Builder.Default
        private String streaks = "public";
        @Builder.Default
        private String groups = "friends";
        @Builder.Default
        private String lastActive = "private";
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPreferences {
        @Builder.Default
        private NotificationSettings notifications = NotificationSettings.builder().build();
        @Builder.Default
        private StudySettings studySettings = StudySettings.builder().build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettings {
        @Builder.Default
        private boolean studyReminders = true;
        @Builder.Default
        private boolean groupUpdates = true;
        @Builder.Default
        private boolean leaderboardChanges = false;
        @Builder.Default
        private boolean friendRequests = true;
        @Builder.Default
        private QuietHours quietHours = QuietHours.builder().build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuietHours {
        @Builder.Default
        private boolean enabled = true;
        @Builder.Default
        private String start = "22:00";
        @Builder.Default
        private String end = "08:00";
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudySettings {
        @Builder.Default
        private Integer maxSessionDuration = 14400;
        @Builder.Default
        private boolean breakReminders = true;
        @Builder.Default
        private boolean autoClockOut = true;
        private String defaultGroup;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceFingerprint {
        private String deviceId;
        private Instant lastSeen;
        private DeviceInfo deviceInfo;
        private boolean isActive;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        private String platform;
        private String version;
        private String model;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityEvent {
        private String type;
        private Instant timestamp;
        private String deviceId;
        private String ipAddress;
        private boolean success;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnalytics {
        @Builder.Default
        private Long totalStudyTime = 0L;
        @Builder.Default
        private Long averageSessionDuration = 0L;
        @Builder.Default
        private Integer longestStreak = 0;
        @Builder.Default
        private Integer currentStreak = 0;
        @Builder.Default
        private List<String> preferredStudyTimes = new ArrayList<>();
        private Instant lastActivityDate;
    }
}



