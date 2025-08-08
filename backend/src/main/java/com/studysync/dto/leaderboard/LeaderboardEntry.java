package com.studysync.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private Long totalSeconds;
    private Integer sessionCount;
    private Double averageDuration;
    private Long longestSession;
    private Integer rank;
    private Integer streakDays;
    private boolean isCurrentUser;
    private String badgeStatus;
}



