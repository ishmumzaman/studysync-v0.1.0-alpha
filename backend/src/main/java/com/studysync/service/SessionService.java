package com.studysync.service;

import com.studysync.dto.session.*;
import com.studysync.exception.ActiveSessionExistsException;
import com.studysync.exception.NoActiveSessionException;
import com.studysync.exception.SessionValidationException;
import com.studysync.model.Session;
import com.studysync.model.User;
import com.studysync.repository.SessionRepository;
import com.studysync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AntiCheatService antiCheatService;
    private final LeaderboardService leaderboardService;

    @Value("${studysync.anti-cheat.max-session-duration}")
    private int maxSessionDuration;

    @Transactional
    public SessionResponse startSession(String userId, SessionStartRequest request) {
        // Check for existing active session
        Optional<Session> activeSession = sessionRepository.findActiveSessionByUserId(userId);
        if (activeSession.isPresent()) {
            // Auto-close stale sessions (older than 8 hours)
            if (isStaleSession(activeSession.get())) {
                endStaleSession(activeSession.get());
            } else {
                throw new ActiveSessionExistsException("User already has an active session");
            }
        }

        // Create new session
        Session session = Session.builder()
            .userId(userId)
            .groupId(request.getGroupId())
            .startTime(Instant.now())
            .status(Session.SessionStatus.ACTIVE)
            .source(Session.SessionSource.builder()
                .platform(request.getPlatform() != null ? request.getPlatform() : "mobile")
                .appVersion(request.getAppVersion())
                .deviceId(request.getDeviceId())
                .build())
            .metadata(Session.SessionMetadata.builder()
                .studySubject(request.getStudySubject())
                .location(request.getLocation())
                .build())
            .build();

        session = sessionRepository.save(session);
        log.info("Session started for user: {} in group: {}", userId, request.getGroupId());

        return mapToSessionResponse(session);
    }

    @Transactional
    @CacheEvict(value = "weekly-leaderboards", allEntries = true)
    public SessionResponse endSession(String userId, SessionEndRequest request) {
        // Find active session
        Session session = sessionRepository.findActiveSessionByUserId(userId)
            .orElseThrow(() -> new NoActiveSessionException("No active session found"));

        // Calculate duration
        Instant endTime = Instant.now();
        long durationSeconds = Duration.between(session.getStartTime(), endTime).getSeconds();

        // Update session
        session.setEndTime(endTime);
        session.setDurationSeconds(durationSeconds);
        session.setStatus(Session.SessionStatus.COMPLETED);

        // Update metadata if provided
        if (request != null) {
            Session.SessionMetadata metadata = session.getMetadata();
            if (metadata == null) {
                metadata = Session.SessionMetadata.builder().build();
            }
            metadata.setMood(request.getMood());
            metadata.setNotes(request.getNotes());
            metadata.setProductivity(request.getProductivity());
            session.setMetadata(metadata);
        }

        // Validate session
        Session.SessionValidation validation = antiCheatService.validateSession(session);
        session.setValidation(validation);

        // Update session status based on validation
        if (validation.getAnomalyScore() > 0.7 || !validation.getFlags().isEmpty()) {
            session.setStatus(Session.SessionStatus.SUSPICIOUS);
            log.warn("Suspicious session detected for user: {}, anomaly score: {}", 
                userId, validation.getAnomalyScore());
        }

        session = sessionRepository.save(session);

        // Update user analytics
        updateUserAnalytics(userId, durationSeconds);

        // Trigger leaderboard update
        if (session.getGroupId() != null) {
            leaderboardService.invalidateLeaderboard(session.getGroupId());
        }

        log.info("Session ended for user: {}, duration: {} seconds", userId, durationSeconds);

        return mapToSessionResponse(session);
    }

    @Transactional(readOnly = true)
    public Optional<SessionResponse> getActiveSession(String userId) {
        return sessionRepository.findActiveSessionByUserId(userId)
            .map(this::mapToSessionResponse);
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getUserSessionHistory(String userId, Pageable pageable) {
        return sessionRepository.findByUserIdOrderByStartTimeDesc(userId, pageable)
            .map(this::mapToSessionResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "user-sessions", key = "#userId + '_' + #startDate + '_' + #endDate")
    public List<SessionResponse> getUserSessionsByDateRange(String userId, Instant startDate, Instant endDate) {
        return sessionRepository.findCompletedSessionsByUserIdAndDateRange(userId, startDate, endDate)
            .stream()
            .map(this::mapToSessionResponse)
            .toList();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void cleanupStaleSessions() {
        Instant staleThreshold = Instant.now().minus(8, ChronoUnit.HOURS);
        List<Session> staleSessions = sessionRepository.findAllStaleActiveSessions(staleThreshold);
        
        for (Session session : staleSessions) {
            endStaleSession(session);
        }
        
        if (!staleSessions.isEmpty()) {
            log.info("Cleaned up {} stale sessions", staleSessions.size());
        }
    }

    private boolean isStaleSession(Session session) {
        Duration activeTime = Duration.between(session.getStartTime(), Instant.now());
        return activeTime.toHours() >= 8;
    }

    private void endStaleSession(Session session) {
        session.setEndTime(Instant.now());
        session.setDurationSeconds(maxSessionDuration);
        session.setStatus(Session.SessionStatus.INVALID);
        
        Session.SessionValidation validation = Session.SessionValidation.builder()
            .serverValidated(true)
            .anomalyScore(1.0)
            .flags(List.of("auto_closed_stale"))
            .validatedAt(Instant.now())
            .build();
        session.setValidation(validation);
        
        sessionRepository.save(session);
        log.info("Auto-closed stale session: {}", session.getId());
    }

    private void updateUserAnalytics(String userId, long sessionDurationSeconds) {
        userRepository.findById(userId).ifPresent(user -> {
            User.UserAnalytics analytics = user.getAnalytics();
            
            // Update total study time
            analytics.setTotalStudyTime(analytics.getTotalStudyTime() + sessionDurationSeconds);
            
            // Update average session duration
            long sessionCount = sessionRepository.countByUserIdAndDateRange(
                userId, 
                Instant.now().minus(30, ChronoUnit.DAYS), 
                Instant.now()
            );
            if (sessionCount > 0) {
                analytics.setAverageSessionDuration(analytics.getTotalStudyTime() / sessionCount);
            }
            
            // Update last activity date
            analytics.setLastActivityDate(Instant.now());
            
            userRepository.save(user);
        });
    }

    private SessionResponse mapToSessionResponse(Session session) {
        SessionResponse response = SessionResponse.builder()
            .id(session.getId())
            .userId(session.getUserId())
            .groupId(session.getGroupId())
            .startTime(session.getStartTime())
            .endTime(session.getEndTime())
            .durationSeconds(session.getDurationSeconds())
            .status(session.getStatus().toString())
            .build();

        if (session.getMetadata() != null) {
            response.setStudySubject(session.getMetadata().getStudySubject());
            response.setLocation(session.getMetadata().getLocation());
            response.setMood(session.getMetadata().getMood());
            response.setNotes(session.getMetadata().getNotes());
            response.setProductivity(session.getMetadata().getProductivity());
        }

        if (session.getValidation() != null) {
            response.setValidation(SessionValidationDto.builder()
                .anomalyScore(session.getValidation().getAnomalyScore())
                .flags(session.getValidation().getFlags())
                .isValid(session.getValidation().isServerValidated())
                .validatedAt(session.getValidation().getValidatedAt())
                .build());
        }

        // Calculate current duration for active sessions
        if (session.getStatus() == Session.SessionStatus.ACTIVE) {
            long currentDuration = Duration.between(session.getStartTime(), Instant.now()).getSeconds();
            response.setCurrentDuration(currentDuration);
        }

        return response;
    }
}



