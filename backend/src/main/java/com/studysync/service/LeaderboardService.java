package com.studysync.service;

import com.studysync.dto.leaderboard.LeaderboardEntry;
import com.studysync.dto.leaderboard.WeeklyLeaderboard;
import com.studysync.model.Session;
import com.studysync.model.User;
import com.studysync.repository.SessionRepository;
import com.studysync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final MongoTemplate mongoTemplate;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "weekly-leaderboards", key = "#groupId + '_' + #week")
    public WeeklyLeaderboard getWeeklyLeaderboard(String groupId, String week) {
        LocalDate weekStart = getWeekStart(week);
        LocalDate weekEnd = weekStart.plusDays(7);
        
        Instant startInstant = weekStart.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = weekEnd.atStartOfDay(ZoneOffset.UTC).toInstant();

        List<LeaderboardEntry> entries = calculateLeaderboard(groupId, startInstant, endInstant);
        
        return WeeklyLeaderboard.builder()
            .groupId(groupId)
            .week(week)
            .weekStart(startInstant)
            .weekEnd(endInstant)
            .entries(entries)
            .totalParticipants(entries.size())
            .build();
    }

    @CacheEvict(value = "weekly-leaderboards", key = "#groupId + '_*'")
    public void invalidateLeaderboard(String groupId) {
        log.info("Invalidating leaderboard cache for group: {}", groupId);
    }

    private List<LeaderboardEntry> calculateLeaderboard(String groupId, Instant startDate, Instant endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
            // Match sessions for the group and date range
            Aggregation.match(Criteria.where("groupId").is(groupId)
                .and("startTime").gte(startDate).lt(endDate)
                .and("status").is("COMPLETED")),
            
            // Group by userId and calculate totals
            Aggregation.group("userId")
                .sum("durationSeconds").as("totalSeconds")
                .count().as("sessionCount")
                .avg("durationSeconds").as("averageDuration")
                .max("durationSeconds").as("longestSession"),
            
            // Sort by total time descending
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalSeconds")),
            
            // Limit to top 50
            Aggregation.limit(50)
        );

        AggregationResults<LeaderboardAggregationResult> results = mongoTemplate.aggregate(
            aggregation, "sessions", LeaderboardAggregationResult.class
        );

        List<LeaderboardAggregationResult> aggregationResults = results.getMappedResults();
        
        // Fetch user details
        List<String> userIds = aggregationResults.stream()
            .map(LeaderboardAggregationResult::getUserId)
            .collect(Collectors.toList());
        
        Map<String, User> userMap = userRepository.findAllById(userIds).stream()
            .collect(Collectors.toMap(User::getId, user -> user));

        // Build leaderboard entries
        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;
        
        for (LeaderboardAggregationResult result : aggregationResults) {
            User user = userMap.get(result.getUserId());
            if (user != null) {
                entries.add(LeaderboardEntry.builder()
                    .userId(result.getUserId())
                    .displayName(user.getDisplayName())
                    .avatarUrl(user.getAvatarUrl())
                    .totalSeconds(result.getTotalSeconds())
                    .sessionCount(result.getSessionCount())
                    .averageDuration(result.getAverageDuration())
                    .longestSession(result.getLongestSession())
                    .rank(rank++)
                    .streakDays(user.getAnalytics().getCurrentStreak())
                    .build());
            }
        }

        return entries;
    }

    private LocalDate getWeekStart(String week) {
        if (week == null || week.isEmpty()) {
            // Current week
            return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        // Parse ISO week format (e.g., "2025-W03")
        String[] parts = week.split("-W");
        int year = Integer.parseInt(parts[0]);
        int weekNumber = Integer.parseInt(parts[1]);
        
        return LocalDate.of(year, 1, 1)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @lombok.Data
    private static class LeaderboardAggregationResult {
        private String userId;
        private Long totalSeconds;
        private Integer sessionCount;
        private Double averageDuration;
        private Long longestSession;
    }
}



