package com.studysync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "groups")
public class Group {
    
    @Id
    private String id;
    
    private String name;
    
    private String description;
    
    private String ownerId;
    
    @Builder.Default
    private List<String> moderatorIds = new ArrayList<>();
    
    @Builder.Default
    @Indexed
    private List<String> memberIds = new ArrayList<>();
    
    @Builder.Default
    private GroupSettings settings = GroupSettings.builder().build();
    
    @Builder.Default
    private List<InviteCode> inviteCodes = new ArrayList<>();
    
    @Builder.Default
    private GroupAnalytics analytics = GroupAnalytics.builder().build();
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupSettings {
        @Builder.Default
        private boolean isPublic = false;
        @Builder.Default
        private Integer maxMembers = 50;
        @Builder.Default
        private boolean requireApproval = true;
        @Builder.Default
        private boolean allowMemberInvites = true;
        @Builder.Default
        private StudyGoals studyGoals = StudyGoals.builder().build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyGoals {
        @Builder.Default
        private Integer weeklyHours = 20;
        @Builder.Default
        private boolean enabled = true;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteCode {
        @Indexed(unique = true, sparse = true)
        private String code;
        private String createdBy;
        private Instant createdAt;
        private Instant expiresAt;
        @Builder.Default
        private Integer usageLimit = 10;
        @Builder.Default
        private Integer usageCount = 0;
        @Builder.Default
        private boolean isActive = true;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupAnalytics {
        @Builder.Default
        private Long totalStudyTime = 0L;
        @Builder.Default
        private Double averageSessionsPerWeek = 0.0;
        @Builder.Default
        private List<String> mostActiveMembers = new ArrayList<>();
        @Builder.Default
        private List<String> peakStudyHours = new ArrayList<>();
        @Builder.Default
        private Double memberRetentionRate = 0.0;
    }
}



