package com.studysync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
@CompoundIndexes({
    @CompoundIndex(name = "user_start_idx", def = "{'userId': 1, 'startTime': -1}"),
    @CompoundIndex(name = "group_start_idx", def = "{'groupId': 1, 'startTime': -1}"),
    @CompoundIndex(name = "status_user_idx", def = "{'status': 1, 'userId': 1}")
})
public class Session {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String groupId;
    
    private Instant startTime;
    
    private Instant endTime;
    
    private Long durationSeconds;
    
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;
    
    @Builder.Default
    private SessionSource source = SessionSource.builder().build();
    
    @Builder.Default
    private SessionValidation validation = SessionValidation.builder().build();
    
    @Builder.Default
    private OfflineSync offline = OfflineSync.builder().build();
    
    private SessionMetadata metadata;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    public enum SessionStatus {
        ACTIVE,
        COMPLETED,
        INVALID,
        SUSPICIOUS
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionSource {
        @Builder.Default
        private String platform = "mobile";
        private String appVersion;
        private String deviceId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionValidation {
        @Builder.Default
        private boolean serverValidated = false;
        @Builder.Default
        private double anomalyScore = 0.0;
        @Builder.Default
        private List<String> flags = new ArrayList<>();
        private Instant validatedAt;
        @Builder.Default
        private ValidationRules validationRules = ValidationRules.builder().build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationRules {
        @Builder.Default
        private boolean maxDuration = true;
        @Builder.Default
        private boolean reasonableHours = true;
        @Builder.Default
        private boolean deviceConsistent = true;
        @Builder.Default
        private boolean timezoneMatch = true;
        @Builder.Default
        private boolean noOverlap = true;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfflineSync {
        @Builder.Default
        private boolean wasOffline = false;
        private Instant syncedAt;
        private String conflictResolution;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionMetadata {
        private String studySubject;
        private String location;
        private String mood;
        private String notes;
        private Integer productivity;
    }
}



