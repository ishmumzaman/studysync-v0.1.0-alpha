package com.studysync.repository;

import com.studysync.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    
    @Query("{ 'userId': ?0, 'status': 'ACTIVE' }")
    Optional<Session> findActiveSessionByUserId(String userId);
    
    @Query("{ 'userId': ?0, 'startTime': { $gte: ?1, $lt: ?2 }, 'status': 'COMPLETED' }")
    List<Session> findCompletedSessionsByUserIdAndDateRange(String userId, Instant startDate, Instant endDate);
    
    @Query("{ 'groupId': ?0, 'startTime': { $gte: ?1, $lt: ?2 }, 'status': 'COMPLETED' }")
    List<Session> findCompletedSessionsByGroupIdAndDateRange(String groupId, Instant startDate, Instant endDate);
    
    @Query("{ 'userId': ?0, 'status': 'ACTIVE', 'createdAt': { $lt: ?1 } }")
    List<Session> findStaleActiveSessions(String userId, Instant beforeTime);
    
    @Query("{ 'status': 'ACTIVE', 'startTime': { $lt: ?0 } }")
    List<Session> findAllStaleActiveSessions(Instant beforeTime);
    
    Page<Session> findByUserIdOrderByStartTimeDesc(String userId, Pageable pageable);
    
    @Query("{ 'validation.anomalyScore': { $gte: ?0 } }")
    List<Session> findSuspiciousSessions(double anomalyThreshold);
    
    @Query("{ 'userId': ?0, 'startTime': { $gte: ?1, $lt: ?2 } }")
    long countByUserIdAndDateRange(String userId, Instant startDate, Instant endDate);
    
    @Query("{ 'groupId': ?0, 'startTime': { $gte: ?1, $lt: ?2 } }")
    long countByGroupIdAndDateRange(String groupId, Instant startDate, Instant endDate);
}



