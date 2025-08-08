package com.studysync.service;

import com.studysync.model.Session;
import com.studysync.model.User;
import com.studysync.repository.SessionRepository;
import com.studysync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AntiCheatService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Value("${studysync.anti-cheat.max-session-duration}")
    private int maxSessionDuration;

    @Value("${studysync.anti-cheat.anomaly-threshold}")
    private double anomalyThreshold;

    public Session.SessionValidation validateSession(Session session) {
        double anomalyScore = calculateAnomalyScore(session);
        List<String> flags = detectSuspiciousPatterns(session);
        
        Session.ValidationRules rules = validateRules(session);
        
        return Session.SessionValidation.builder()
            .serverValidated(true)
            .anomalyScore(anomalyScore)
            .flags(flags)
            .validatedAt(Instant.now())
            .validationRules(rules)
            .build();
    }

    private double calculateAnomalyScore(Session session) {
        Optional<User> userOpt = userRepository.findById(session.getUserId());
        if (userOpt.isEmpty()) {
            return 1.0;
        }

        User user = userOpt.get();
        User.UserAnalytics analytics = user.getAnalytics();
        
        double durationScore = calculateDurationAnomalyScore(session, analytics);
        double timeScore = calculateTimeAnomalyScore(session, analytics);
        double patternScore = calculatePatternAnomalyScore(session, analytics);
        double deviceScore = calculateDeviceAnomalyScore(session, user);
        
        // Weighted average of scores
        return (durationScore * 0.4 + timeScore * 0.2 + 
                patternScore * 0.3 + deviceScore * 0.1);
    }

    private double calculateDurationAnomalyScore(Session session, User.UserAnalytics analytics) {
        if (session.getDurationSeconds() == null) {
            return 0.0;
        }

        // Check if duration exceeds maximum
        if (session.getDurationSeconds() > maxSessionDuration) {
            return 1.0;
        }

        // Compare with user's average
        if (analytics.getAverageSessionDuration() > 0) {
            double deviation = Math.abs(session.getDurationSeconds() - analytics.getAverageSessionDuration());
            double relativeDeviation = deviation / analytics.getAverageSessionDuration();
            
            // Normalize to 0-1 scale
            return Math.min(relativeDeviation / 3.0, 1.0);
        }

        return 0.0;
    }

    private double calculateTimeAnomalyScore(Session session, User.UserAnalytics analytics) {
        LocalTime sessionTime = LocalTime.from(session.getStartTime().atZone(ZoneOffset.UTC));
        
        // Check if session is during unusual hours (2 AM - 5 AM)
        if (sessionTime.isAfter(LocalTime.of(2, 0)) && sessionTime.isBefore(LocalTime.of(5, 0))) {
            // Check if user has a pattern of studying at night
            if (!isNightOwlUser(analytics)) {
                return 0.8;
            }
        }

        return 0.0;
    }

    private double calculatePatternAnomalyScore(Session session, User.UserAnalytics analytics) {
        // Check for rapid succession sessions
        Instant recentThreshold = session.getStartTime().minus(1, ChronoUnit.HOURS);
        List<Session> recentSessions = sessionRepository.findCompletedSessionsByUserIdAndDateRange(
            session.getUserId(),
            recentThreshold,
            session.getStartTime()
        );

        if (recentSessions.size() > 3) {
            return 0.9; // Too many sessions in short time
        }

        return 0.0;
    }

    private double calculateDeviceAnomalyScore(Session session, User user) {
        if (session.getSource() == null || session.getSource().getDeviceId() == null) {
            return 0.5;
        }

        // Check if device is known
        boolean deviceKnown = user.getDeviceFingerprints().stream()
            .anyMatch(fp -> fp.getDeviceId().equals(session.getSource().getDeviceId()));

        return deviceKnown ? 0.0 : 0.3;
    }

    private List<String> detectSuspiciousPatterns(Session session) {
        List<String> flags = new ArrayList<>();

        if (session.getDurationSeconds() != null) {
            // Excessive duration
            if (session.getDurationSeconds() > maxSessionDuration) {
                flags.add("excessive_duration");
            }

            // Perfect round numbers (suspicious)
            if (isPerfectRoundNumber(session.getDurationSeconds())) {
                flags.add("round_number_duration");
            }

            // Very short session
            if (session.getDurationSeconds() < 60) {
                flags.add("very_short_session");
            }
        }

        // Overnight session
        if (isOvernightSession(session)) {
            flags.add("overnight_session");
        }

        // Weekend marathon (more than 8 hours on weekend)
        if (isWeekendMarathon(session)) {
            flags.add("weekend_marathon");
        }

        return flags;
    }

    private Session.ValidationRules validateRules(Session session) {
        return Session.ValidationRules.builder()
            .maxDuration(session.getDurationSeconds() == null || 
                        session.getDurationSeconds() <= maxSessionDuration)
            .reasonableHours(isReasonableHours(session))
            .deviceConsistent(isDeviceConsistent(session))
            .timezoneMatch(isTimezoneMatch(session))
            .noOverlap(hasNoOverlap(session))
            .build();
    }

    private boolean isNightOwlUser(User.UserAnalytics analytics) {
        if (analytics.getPreferredStudyTimes() == null) {
            return false;
        }
        
        return analytics.getPreferredStudyTimes().stream()
            .anyMatch(time -> time.contains("00:00") || time.contains("01:00") || 
                            time.contains("02:00") || time.contains("03:00"));
    }

    private boolean isPerfectRoundNumber(long seconds) {
        // Check if duration is exactly in hours (3600 seconds)
        return seconds % 3600 == 0 && seconds >= 3600;
    }

    private boolean isOvernightSession(Session session) {
        if (session.getStartTime() == null || session.getEndTime() == null) {
            return false;
        }

        LocalDateTime start = LocalDateTime.ofInstant(session.getStartTime(), ZoneOffset.UTC);
        LocalDateTime end = LocalDateTime.ofInstant(session.getEndTime(), ZoneOffset.UTC);
        
        // Check if session spans midnight
        return !start.toLocalDate().equals(end.toLocalDate());
    }

    private boolean isWeekendMarathon(Session session) {
        if (session.getStartTime() == null || session.getDurationSeconds() == null) {
            return false;
        }

        DayOfWeek dayOfWeek = LocalDateTime.ofInstant(session.getStartTime(), ZoneOffset.UTC).getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        
        return isWeekend && session.getDurationSeconds() > 28800; // 8 hours
    }

    private boolean isReasonableHours(Session session) {
        LocalTime startTime = LocalTime.from(session.getStartTime().atZone(ZoneOffset.UTC));
        return !startTime.isAfter(LocalTime.of(2, 0)) || !startTime.isBefore(LocalTime.of(5, 0));
    }

    private boolean isDeviceConsistent(Session session) {
        // For now, assume device is consistent if source is provided
        return session.getSource() != null && session.getSource().getDeviceId() != null;
    }

    private boolean isTimezoneMatch(Session session) {
        // TODO: Implement timezone validation based on user's timezone
        return true;
    }

    private boolean hasNoOverlap(Session session) {
        // Check for overlapping sessions
        List<Session> overlappingSessions = sessionRepository.findCompletedSessionsByUserIdAndDateRange(
            session.getUserId(),
            session.getStartTime(),
            session.getEndTime() != null ? session.getEndTime() : Instant.now()
        );

        // Filter out current session
        overlappingSessions = overlappingSessions.stream()
            .filter(s -> !s.getId().equals(session.getId()))
            .toList();

        return overlappingSessions.isEmpty();
    }
}



