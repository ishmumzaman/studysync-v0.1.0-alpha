package com.studysync.repository;

import com.studysync.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByDisplayNameContainingIgnoreCase(String name);
    
    @Query("{ 'friends': ?0 }")
    List<User> findByFriendsContaining(String userId);
    
    @Query("{ 'groups': ?0 }")
    List<User> findByGroupsContaining(String groupId);
    
    @Query("{ 'analytics.lastActivityDate': { $gte: ?0 } }")
    List<User> findActiveUsersSince(Instant date);
    
    @Query("{ 'deviceFingerprints.deviceId': ?0 }")
    Optional<User> findByDeviceId(String deviceId);
    
    @Query("{ '_id': ?0 }", fields = "{ 'passwordHash': 0 }")
    Optional<User> findByIdWithoutPassword(String id);
}



