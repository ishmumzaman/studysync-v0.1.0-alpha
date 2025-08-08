package com.studysync.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyLeaderboard {
    private String groupId;
    private String week;
    private Instant weekStart;
    private Instant weekEnd;
    private List<LeaderboardEntry> entries;
    private Integer totalParticipants;
    private Double groupAverage;
    private Integer myRank;
    private LeaderboardEntry myEntry;
}



