# StudySync - Enhanced Technical Architecture Plan

## Product Overview

### Core Concept
StudySync is a social study tracking mobile application that gamifies learning through competitive time tracking, social accountability, and intelligent engagement systems. The platform enables students to form study groups, track authentic study sessions, and participate in weekly leaderboards while maintaining data integrity through advanced anti-cheat mechanisms.

### Technical Product Vision
A cross-platform mobile application built on React Native with a robust Spring Boot backend, featuring real-time synchronization, offline-first architecture, and enterprise-grade security. The system prioritizes data accuracy, user engagement, and scalability while providing sub-second response times for core interactions.

### Key Features & Technical Implementation

#### 1. Authenticated Study Session Tracking
- **Server-authoritative timing**: All session start/end timestamps generated server-side to prevent manipulation
- **Multi-device session management**: Single active session per user across all devices with automatic conflict resolution
- **Offline-resilient architecture**: Local session caching with intelligent sync and conflict resolution upon reconnection
- **Anti-cheat validation**: Machine learning-based anomaly detection for suspicious study patterns
- **Device fingerprinting**: Hardware-based session validation to prevent spoofing

#### 2. Social Study Groups & Friend Management
- **Hierarchical group system**: Groups with owners, moderators, and members with granular permission controls
- **Dynamic invite system**: Time-limited, single-use invite codes with optional capacity limits
- **Friend discovery**: Privacy-aware friend search with mutual connection requirements
- **Group analytics**: Real-time engagement metrics and participation insights

#### 3. Real-time Competitive Leaderboards
- **Multi-tiered caching**: Redis-backed leaderboard caching with 30-second refresh cycles
- **Live updates**: WebSocket-based real-time leaderboard updates across all group members
- **Historical tracking**: Week-over-week progression analytics with trend visualization
- **Fair competition**: Timezone-aware weekly cycles with configurable competition periods

#### 4. Intelligent Engagement System
- **Behavioral analytics**: ML-driven user engagement pattern analysis
- **Adaptive notifications**: Personalized study reminders based on historical activity patterns
- **Streak tracking**: Configurable study streak systems with reward mechanisms
- **Social pressure mechanisms**: Gentle peer accountability through group visibility controls

#### 5. Privacy-First Design
- **Granular privacy controls**: User-level visibility settings for study time, streaks, and group participation
- **Data minimization**: Zero unnecessary data collection with explicit consent for analytics
- **GDPR/CCPA compliance**: Built-in data export, deletion, and portability features
- **End-to-end encryption**: Sensitive user data encrypted at rest and in transit

### Technical Architecture Philosophy

#### Performance Requirements
- **API Response Times**: P95 < 200ms for reads, P95 < 500ms for complex aggregations
- **Mobile App Performance**: <2s cold start, <500ms navigation transitions
- **Real-time Updates**: <100ms WebSocket message delivery
- **Offline Capability**: Full session tracking functionality offline with <5s sync recovery

#### Scalability Targets
- **Concurrent Users**: 10,000+ simultaneous active sessions
- **Data Volume**: 1M+ sessions per month with 99.9% accuracy
- **Group Scale**: Support for groups up to 1,000 members
- **Geographic Distribution**: Multi-region deployment with <150ms cross-region latency

#### Reliability Standards
- **Uptime SLA**: 99.9% availability (8.76 hours downtime/year maximum)
- **Data Integrity**: 99.99% session accuracy with automated validation
- **Disaster Recovery**: <4 hour RTO, <15 minute RPO
- **Security**: Zero-trust architecture with defense-in-depth principles

## Enhanced Architecture

### Client Architecture (React Native)

#### Technology Stack
```typescript
// Core Framework
- React Native 0.73+ (New Architecture enabled)
- TypeScript 5.0+ (strict mode)
- Metro bundler with Hermes engine

// Navigation & State Management
- @react-navigation/native 6.x (type-safe navigation)
- @tanstack/react-query 5.x (server state management)
- Zustand 4.x (client state management)
- React Hook Form (form state & validation)

// Storage & Caching
- @react-native-async-storage/async-storage (general storage)
- expo-secure-store (sensitive data)
- SQLite (offline session queue)
- React Query persistent cache

// Real-time & Networking
- @react-native-websocket (real-time updates)
- axios (HTTP client with interceptors)
- react-native-network-info (connectivity monitoring)

// Security & Authentication
- @react-native-keychain (secure token storage)
- expo-crypto (local encryption)
- react-native-device-info (device fingerprinting)

// UI & UX
- react-native-paper (Material Design 3)
- react-native-svg (vector graphics)
- react-native-reanimated 3.x (60fps animations)
- react-native-haptic-feedback
- react-native-safe-area-context

// Push Notifications & Background
- @react-native-firebase/messaging (FCM)
- @react-native-async-storage/async-storage
- react-native-background-timer (session tracking)

// Development & Monitoring
- Flipper (debugging)
- Sentry React Native (error tracking)
- react-native-performance (performance monitoring)
```

#### App Structure
```
src/
├── components/          # Reusable UI components
│   ├── common/         # Generic components
│   ├── forms/          # Form-specific components
│   └── charts/         # Data visualization
├── screens/            # Screen components
│   ├── auth/           # Authentication flows
│   ├── study/          # Study session management
│   ├── groups/         # Group management
│   ├── leaderboard/    # Competition views
│   └── profile/        # User profile
├── navigation/         # Navigation configuration
├── services/           # API and business logic
│   ├── api/            # API client
│   ├── auth/           # Authentication service
│   ├── sync/           # Offline sync logic
│   └── notifications/ # Push notification handling
├── store/              # State management
├── utils/              # Utility functions
├── types/              # TypeScript type definitions
└── config/             # App configuration
```

#### Offline-First Architecture
```typescript
interface OfflineAction {
  id: string;
  type: 'CLOCK_IN' | 'CLOCK_OUT' | 'JOIN_GROUP' | 'SEND_FRIEND_REQUEST';
  payload: any;
  timestamp: number;
  deviceId: string;
  retryCount: number;
  maxRetries: number;
}

class OfflineManager {
  private queue: OfflineAction[] = [];
  private isOnline: boolean = true;
  
  async processOfflineQueue(): Promise<void> {
    // Intelligent retry with exponential backoff
    // Conflict resolution for competing actions
    // User notification for unresolvable conflicts
  }
}
```

### Backend Architecture (Spring Boot)

#### Enhanced Technology Stack
```xml
<!-- Core Spring Boot -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <version>3.2+</version>
</dependency>

<!-- Security & Authentication -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.12.3</version>
</dependency>

<!-- Database & Caching -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Real-time Communication -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Monitoring & Observability -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- Validation & Documentation -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.2.0</version>
</dependency>

<!-- Rate Limiting & Security -->
<dependency>
  <groupId>com.github.vladimir-bukhtoyarov</groupId>
  <artifactId>bucket4j-core</artifactId>
  <version>7.6.0</version>
</dependency>

<!-- Testing -->
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>mongodb</artifactId>
  <scope>test</scope>
</dependency>
```

#### Service Architecture
```
backend/
├── src/main/java/com/studysync/
│   ├── config/                 # Configuration classes
│   │   ├── SecurityConfig.java
│   │   ├── MongoConfig.java
│   │   ├── RedisConfig.java
│   │   ├── WebSocketConfig.java
│   │   └── CacheConfig.java
│   ├── controller/             # REST endpoints
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── SessionController.java
│   │   ├── GroupController.java
│   │   └── LeaderboardController.java
│   ├── service/                # Business logic
│   │   ├── AuthService.java
│   │   ├── SessionService.java
│   │   ├── GroupService.java
│   │   ├── LeaderboardService.java
│   │   ├── NotificationService.java
│   │   └── AntiCheatService.java
│   ├── repository/             # Data access layer
│   │   ├── UserRepository.java
│   │   ├── SessionRepository.java
│   │   ├── GroupRepository.java
│   │   └── custom/             # Custom aggregation repos
│   ├── model/                  # Entity classes
│   │   ├── User.java
│   │   ├── Session.java
│   │   ├── Group.java
│   │   └── validators/         # Custom validators
│   ├── dto/                    # Data transfer objects
│   ├── security/               # Security components
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── DeviceFingerprint.java
│   ├── websocket/              # WebSocket handlers
│   ├── scheduler/              # Background jobs
│   └── exception/              # Exception handling
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    └── db/migration/           # Database migration scripts
```

### Enhanced Data Model

#### Users Collection
```json
{
  "_id": "user_abc123",
  "email": "student@university.edu",
  "passwordHash": "$2a$12$encrypted_password_hash",
  "displayName": "Ishmum Rahman",
  "avatarUrl": "https://cdn.studysync.app/avatars/user_abc123.webp",
  "timezone": "America/New_York",
  "profileVisibility": {
    "studyTime": "friends",        // public, friends, private
    "streaks": "public",
    "groups": "friends",
    "lastActive": "private"
  },
  "preferences": {
    "notifications": {
      "studyReminders": true,
      "groupUpdates": true,
      "leaderboardChanges": false,
      "friendRequests": true,
      "quietHours": {
        "enabled": true,
        "start": "22:00",
        "end": "08:00"
      }
    },
    "studySettings": {
      "maxSessionDuration": 14400,    // 4 hours in seconds
      "breakReminders": true,
      "autoClockOut": true,
      "defaultGroup": "group_1"
    }
  },
  "groups": ["group_1", "group_2"],
  "friends": ["user_xyz789"],
  "deviceFingerprints": [
    {
      "deviceId": "device_android_123",
      "lastSeen": ISODate("2025-01-15T10:30:00Z"),
      "deviceInfo": {
        "platform": "android",
        "version": "14",
        "model": "Pixel 7"
      },
      "isActive": true
    }
  ],
  "securityEvents": [
    {
      "type": "LOGIN",
      "timestamp": ISODate("2025-01-15T09:00:00Z"),
      "deviceId": "device_android_123",
      "ipAddress": "192.168.1.100",
      "success": true
    }
  ],
  "analytics": {
    "totalStudyTime": 144000,        // Total seconds across all time
    "averageSessionDuration": 3600,
    "longestStreak": 21,
    "currentStreak": 5,
    "preferredStudyTimes": ["09:00-12:00", "14:00-17:00"],
    "lastActivityDate": ISODate("2025-01-15T16:00:00Z")
  },
  "createdAt": ISODate("2024-12-01T10:00:00Z"),
  "updatedAt": ISODate("2025-01-15T16:00:00Z"),
  "version": 1                     // For optimistic locking
}
```

**Indexes:**
```javascript
// Unique indexes
db.users.createIndex({ "email": 1 }, { unique: true })
db.users.createIndex({ "deviceFingerprints.deviceId": 1 })

// Query optimization indexes
db.users.createIndex({ "displayName": "text" })  // Text search
db.users.createIndex({ "friends": 1 })
db.users.createIndex({ "groups": 1 })
db.users.createIndex({ "analytics.lastActivityDate": -1 })
```

#### Enhanced Sessions Collection
```json
{
  "_id": "sess_abc123",
  "userId": "user_abc123",
  "groupId": "group_1",           // null for solo sessions
  "startTime": ISODate("2025-01-15T14:00:00Z"),
  "endTime": ISODate("2025-01-15T16:15:00Z"),  // null if active
  "durationSeconds": 8100,        // Computed on session end
  "status": "completed",          // active, completed, invalid, suspicious
  "source": {
    "platform": "mobile",
    "appVersion": "1.2.3",
    "deviceId": "device_android_123"
  },
  "validation": {
    "serverValidated": true,
    "anomalyScore": 0.15,         // ML-computed suspicion score (0-1)
    "flags": [],                  // ["long_duration", "overnight", "rapid_succession"]
    "validatedAt": ISODate("2025-01-15T16:15:30Z"),
    "validationRules": {
      "maxDuration": true,        // Under 4 hour limit
      "reasonableHours": true,    // During typical study hours
      "deviceConsistent": true,   // Same device start/end
      "timezoneMatch": true,      // Matches user timezone
      "noOverlap": true          // No overlapping sessions
    }
  },
  "offline": {
    "wasOffline": false,
    "syncedAt": null,
    "conflictResolution": null
  },
  "metadata": {
    "studySubject": "Computer Science",  // Optional user tag
    "location": "Library",               // Optional user tag
    "mood": "focused",                   // Optional user rating
    "notes": "Working on algorithms"     // Optional user notes
  },
  "createdAt": ISODate("2025-01-15T14:00:00Z"),
  "updatedAt": ISODate("2025-01-15T16:15:30Z")
}
```

**Indexes:**
```javascript
// Query optimization
db.sessions.createIndex({ "userId": 1, "startTime": -1 })
db.sessions.createIndex({ "groupId": 1, "startTime": -1 })
db.sessions.createIndex({ "status": 1, "userId": 1 })
db.sessions.createIndex({ "validation.anomalyScore": -1 })  // Anti-cheat queries

// TTL for cleanup of invalid sessions
db.sessions.createIndex({ "createdAt": 1 }, { 
  expireAfterSeconds: 7776000,  // 90 days
  partialFilterExpression: { "status": "invalid" }
})
```

#### Groups Collection
```json
{
  "_id": "group_abc123",
  "name": "CS Study Group Fall 2025",
  "description": "Computer Science students studying together",
  "ownerId": "user_abc123",
  "moderatorIds": ["user_xyz789"],
  "memberIds": ["user_abc123", "user_xyz789", "user_def456"],
  "settings": {
    "isPublic": false,
    "maxMembers": 50,
    "requireApproval": true,
    "allowMemberInvites": true,
    "studyGoals": {
      "weeklyHours": 20,
      "enabled": true
    }
  },
  "inviteCodes": [
    {
      "code": "K9R4FQ",
      "createdBy": "user_abc123",
      "createdAt": ISODate("2025-01-15T10:00:00Z"),
      "expiresAt": ISODate("2025-01-22T10:00:00Z"),
      "usageLimit": 10,
      "usageCount": 3,
      "isActive": true
    }
  ],
  "analytics": {
    "totalStudyTime": 432000,      // Group total in seconds
    "averageSessionsPerWeek": 15,
    "mostActiveMembers": ["user_abc123", "user_xyz789"],
    "peakStudyHours": ["14:00-17:00"],
    "memberRetentionRate": 0.85
  },
  "tags": ["computer-science", "university", "study-group"],
  "createdAt": ISODate("2025-01-01T10:00:00Z"),
  "updatedAt": ISODate("2025-01-15T16:00:00Z")
}
```

#### Friend Requests Collection
```json
{
  "_id": "freq_abc123",
  "fromUserId": "user_abc123",
  "toUserId": "user_xyz789",
  "status": "pending",            // pending, accepted, rejected, expired
  "message": "Hey! Let's study together this semester",
  "expiresAt": ISODate("2025-01-22T10:00:00Z"),  // 7 days from creation
  "respondedAt": null,
  "createdAt": ISODate("2025-01-15T10:00:00Z"),
  "updatedAt": ISODate("2025-01-15T10:00:00Z")
}
```

#### Weekly Aggregates (Cache) Collection
```json
{
  "_id": "agg_user_abc123_2025W03",
  "userId": "user_abc123",
  "isoWeek": "2025-W03",
  "weekStart": ISODate("2025-01-13T00:00:00Z"),
  "weekEnd": ISODate("2025-01-19T23:59:59Z"),
  "totalSeconds": 18000,          // 5 hours total
  "sessionCount": 6,
  "averageSessionDuration": 3000,
  "longestSession": 4500,
  "studyDays": 4,                 // Days with at least one session
  "groupTotals": [
    {
      "groupId": "group_abc123",
      "totalSeconds": 14400,
      "sessionCount": 4,
      "rank": 2,                  // Current rank in group
      "memberCount": 15           // Total members in group
    }
  ],
  "streakData": {
    "weeklyStreak": 3,           // Consecutive weeks with study time
    "currentDailyStreak": 4,     // Consecutive days
    "longestDailyStreak": 21
  },
  "computedAt": ISODate("2025-01-19T23:59:59Z"),
  "isComplete": true              // Whether week is finished
}
```

### Anti-Cheat & Session Validation System

#### ML-Based Anomaly Detection
```java
@Service
public class AntiCheatService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public ValidationResult validateSession(Session session) {
        double anomalyScore = calculateAnomalyScore(session);
        List<String> flags = detectSuspiciousPatterns(session);
        
        ValidationResult result = ValidationResult.builder()
            .anomalyScore(anomalyScore)
            .flags(flags)
            .isValid(anomalyScore < 0.7 && flags.isEmpty())
            .build();
            
        if (!result.isValid()) {
            // Flag for manual review or auto-reject
            handleSuspiciousSession(session, result);
        }
        
        return result;
    }
    
    private double calculateAnomalyScore(Session session) {
        // Factors considered:
        // - Session duration vs user average (z-score)
        // - Time of day patterns
        // - Device consistency
        // - Rapid succession sessions
        // - Weekend vs weekday patterns
        // - Break patterns between sessions
        
        UserAnalytics analytics = getUserAnalytics(session.getUserId());
        
        double durationScore = calculateDurationAnomalyScore(session, analytics);
        double timeScore = calculateTimeAnomalyScore(session, analytics);
        double patternScore = calculatePatternAnomalyScore(session, analytics);
        double deviceScore = calculateDeviceAnomalyScore(session);
        
        return (durationScore * 0.4 + timeScore * 0.2 + 
                patternScore * 0.3 + deviceScore * 0.1);
    }
    
    private List<String> detectSuspiciousPatterns(Session session) {
        List<String> flags = new ArrayList<>();
        
        // Duration checks
        if (session.getDurationSeconds() > MAX_SESSION_DURATION) {
            flags.add("excessive_duration");
        }
        
        // Overnight sessions (unless user has night owl pattern)
        if (isOvernightSession(session) && !isNightOwlUser(session.getUserId())) {
            flags.add("overnight_session");
        }
        
        // Too many sessions in short time
        if (hasRapidSuccessionSessions(session.getUserId(), session.getStartTime())) {
            flags.add("rapid_succession");
        }
        
        // Perfect round numbers (suspicious)
        if (isPerfectRoundNumber(session.getDurationSeconds())) {
            flags.add("round_number_duration");
        }
        
        // Device switching mid-session
        if (hasDeviceSwitching(session)) {
            flags.add("device_switching");
        }
        
        return flags;
    }
}
```

#### Real-time Session Monitoring
```java
@Component
public class SessionMonitor {
    
    private final Map<String, ActiveSession> activeSessions = new ConcurrentHashMap<>();
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void validateActiveSessions() {
        activeSessions.values().parallelStream()
            .filter(this::isStaleSession)
            .forEach(this::handleStaleSession);
    }
    
    private boolean isStaleSession(ActiveSession session) {
        // Auto-close sessions that have been active too long
        Duration activeTime = Duration.between(session.getStartTime(), Instant.now());
        return activeTime.toHours() > MAX_ACTIVE_HOURS;
    }
    
    @EventListener
    public void handleDeviceDisconnect(DeviceDisconnectEvent event) {
        // Handle graceful session cleanup when device goes offline
        ActiveSession session = activeSessions.get(event.getUserId());
        if (session != null) {
            // Mark for offline sync validation
            session.setOfflineSync(true);
            session.setLastHeartbeat(event.getTimestamp());
        }
    }
}
```

### Enhanced Caching Strategy

#### Multi-Layer Caching Architecture
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
            
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build();
    }
    
    // Cache configurations for different data types
    @Bean
    @Primary
    public RedisCacheManager leaderboardCacheManager(RedisConnectionFactory factory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
            "weekly-leaderboards", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)),  // Frequent updates
            "user-profiles", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)),    // Less frequent updates
            "group-metadata", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))   // Moderate updates
        );
        
        return RedisCacheManager.builder(factory)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
```

#### Smart Cache Invalidation
```java
@Service
public class LeaderboardService {
    
    @Cacheable(value = "weekly-leaderboards", key = "#groupId + '_' + #week")
    public WeeklyLeaderboard getWeeklyLeaderboard(String groupId, String week) {
        return calculateLeaderboard(groupId, week);
    }
    
    @CacheEvict(value = "weekly-leaderboards", key = "#groupId + '_' + #week")
    public void invalidateLeaderboard(String groupId, String week) {
        // Called when new session is completed
    }
    
    // Pre-warm cache for popular groups
    @Scheduled(cron = "0 */5 * * * *")  // Every 5 minutes
    public void preWarmPopularLeaderboards() {
        List<String> popularGroups = getPopularGroups();
        String currentWeek = getCurrentISOWeek();
        
        popularGroups.parallelStream()
            .forEach(groupId -> getWeeklyLeaderboard(groupId, currentWeek));
    }
}
```

### Real-time Communication System

#### WebSocket Configuration
```java
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new AuthChannelInterceptor());
        registration.taskExecutor(new ThreadPoolTaskExecutor());
    }
}

@Controller
public class LeaderboardWebSocketController {
    
    @MessageMapping("/group/{groupId}/join")
    public void joinGroup(@DestinationVariable String groupId, Principal principal) {
        // Subscribe user to group updates
        messagingTemplate.convertAndSendToUser(
            principal.getName(),
            "/queue/group-joined",
            new GroupJoinResponse(groupId)
        );
    }
    
    @EventListener
    public void handleSessionUpdate(SessionCompletedEvent event) {
        // Notify all group members of leaderboard changes
        event.getGroupIds().forEach(groupId -> {
            WeeklyLeaderboard updated = leaderboardService.getWeeklyLeaderboard(
                groupId, getCurrentWeek());
                
            messagingTemplate.convertAndSend(
                "/topic/group/" + groupId + "/leaderboard",
                updated
            );
        });
    }
}
```

#### Mobile WebSocket Integration
```typescript
// services/websocket.ts
class WebSocketService {
  private stompClient: Client;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  
  constructor() {
    this.stompClient = new Client({
      brokerURL: 'wss://api.studysync.app/ws',
      connectHeaders: {
        Authorization: `Bearer ${getAuthToken()}`
      },
      reconnectDelay: this.calculateReconnectDelay(),
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });
    
    this.stompClient.onConnect = this.handleConnect.bind(this);
    this.stompClient.onStompError = this.handleError.bind(this);
    this.stompClient.onWebSocketClose = this.handleClose.bind(this);
  }
  
  subscribeToGroupUpdates(groupId: string, callback: (update: any) => void) {
    this.stompClient.subscribe(
      `/topic/group/${groupId}/leaderboard`,
      (message) => {
        const leaderboard = JSON.parse(message.body);
        callback(leaderboard);
      }
    );
  }
  
  private calculateReconnectDelay(): number {
    // Exponential backoff with jitter
    const baseDelay = 1000;
    const exponentialDelay = Math.pow(2, this.reconnectAttempts) * baseDelay;
    const jitter = Math.random() * 0.1 * exponentialDelay;
    return Math.min(exponentialDelay + jitter, 30000); // Max 30 seconds
  }
}
```

### Security Architecture

#### JWT Token Management
```java
@Component
public class JwtTokenProvider {
    
    private final String jwtSecret;
    private final long accessTokenExpiration = 900000; // 15 minutes
    private final long refreshTokenExpiration = 2592000000L; // 30 days
    
    public TokenPair generateTokenPair(String userId, String deviceId) {
        String accessToken = createAccessToken(userId, deviceId);
        String refreshToken = createRefreshToken(userId, deviceId);
        
        // Store refresh token with device association
        redisTemplate.opsForValue().set(
            "refresh_token:" + refreshToken,
            new RefreshTokenData(userId, deviceId),
            Duration.ofDays(30)
        );
        
        return new TokenPair(accessToken, refreshToken);
    }
    
    public TokenPair refreshTokens(String refreshToken) {
        RefreshTokenData tokenData = getRefreshTokenData(refreshToken);
        if (tokenData == null || isTokenBlacklisted(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        
        // Rotate refresh token for security
        invalidateRefreshToken(refreshToken);
        return generateTokenPair(tokenData.getUserId(), tokenData.getDeviceId());
    }
    
    private String createAccessToken(String userId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("device_id", deviceId);
        claims.put("token_type", "access");
        claims.put("issued_at", System.currentTimeMillis());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
}
```

#### Device Fingerprinting
```java
@Service
public class DeviceFingerprintService {
    
    public DeviceFingerprint generateFingerprint(HttpServletRequest request) {
        return DeviceFingerprint.builder()
            .userAgent(request.getHeader("User-Agent"))
            .acceptLanguage(request.getHeader("Accept-Language"))
            .screenResolution(request.getHeader("X-Screen-Resolution"))
            .timezone(request.getHeader("X-Timezone"))
            .platform(request.getHeader("X-Platform"))
            .appVersion(request.getHeader("X-App-Version"))
            .deviceModel(request.getHeader("X-Device-Model"))
            .ipAddress(getClientIpAddress(request))
            .fingerprint(calculateFingerprint(request))
            .build();
    }
    
    public boolean validateDevice(String userId, DeviceFingerprint fingerprint) {
        List<DeviceFingerprint> knownDevices = getUserDevices(userId);
        
        return knownDevices.stream()
            .anyMatch(known -> calculateSimilarity(known, fingerprint) > 0.8);
    }
    
    private double calculateSimilarity(DeviceFingerprint known, DeviceFingerprint current) {
        // Calculate similarity score based on multiple factors
        double userAgentSimilarity = stringSimilarity(known.getUserAgent(), current.getUserAgent());
        double platformMatch = known.getPlatform().equals(current.getPlatform()) ? 1.0 : 0.0;
        double timezoneMatch = known.getTimezone().equals(current.getTimezone()) ? 1.0 : 0.5;
        
        return (userAgentSimilarity * 0.4 + platformMatch * 0.4 + timezoneMatch * 0.2);
    }
}
```

#### Rate Limiting & DDoS Protection
```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public Bucket createNewUserBucket() {
        return Bucket4j.builder()
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
            .build();
    }
    
    @Component
    public class RateLimitingFilter implements Filter {
        
        private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, 
                           FilterChain chain) throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String key = getRateLimitKey(httpRequest);
            
            Bucket bucket = cache.computeIfAbsent(key, this::createBucket);
            
            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(429);
                httpResponse.getWriter().write("Rate limit exceeded");
            }
        }
        
        private String getRateLimitKey(HttpServletRequest request) {
            String userId = extractUserId(request);
            String ipAddress = getClientIpAddress(request);
            String endpoint = request.getRequestURI();
            
            // Different rate limits for different combinations
            if (userId != null) {
                return "user:" + userId + ":" + endpoint;
            } else {
                return "ip:" + ipAddress + ":" + endpoint;
            }
        }
    }
}
```

### Comprehensive Monitoring & Analytics

#### Application Metrics
```java
@Component
public class CustomMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter sessionStartCounter;
    private final Timer sessionDurationTimer;
    private final Gauge activeSessionsGauge;
    
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.sessionStartCounter = Counter.builder("studysync.sessions.started")
            .description("Number of study sessions started")
            .tag("application", "studysync")
            .register(meterRegistry);
            
        this.sessionDurationTimer = Timer.builder("studysync.sessions.duration")
            .description("Study session duration")
            .register(meterRegistry);
            
        this.activeSessionsGauge = Gauge.builder("studysync.sessions.active")
            .description("Currently active study sessions")
            .register(meterRegistry, this, CustomMetrics::getActiveSessionCount);
    }
    
    public void recordSessionStart(String groupId) {
        sessionStartCounter.increment(
            Tags.of("group", groupId != null ? "group" : "solo")
        );
    }
    
    public void recordSessionEnd(Duration duration, String groupId) {
        sessionDurationTimer.record(duration,
            Tags.of("group", groupId != null ? "group" : "solo")
        );
    }
    
    private double getActiveSessionCount() {
        return sessionService.getActiveSessionCount();
    }
}
```

#### User Behavior Analytics
```java
@Service
public class AnalyticsService {
    
    @EventListener
    @Async
    public void handleUserAction(UserActionEvent event) {
        AnalyticsData data = AnalyticsData.builder()
            .userId(event.getUserId())
            .action(event.getAction())
            .timestamp(event.getTimestamp())
            .metadata(event.getMetadata())
            .sessionId(event.getSessionId())
            .build();
            
        // Send to analytics pipeline (async)
        analyticsQueue.send(data);
    }
    
    public UserEngagementReport generateEngagementReport(String userId, LocalDate startDate, LocalDate endDate) {
        return UserEngagementReport.builder()
            .totalStudyTime(calculateTotalStudyTime(userId, startDate, endDate))
            .sessionCount(getSessionCount(userId, startDate, endDate))
            .averageSessionDuration(calculateAverageSessionDuration(userId, startDate, endDate))
            .streakData(calculateStreakData(userId, endDate))
            .groupParticipation(getGroupParticipation(userId, startDate, endDate))
            .engagementScore(calculateEngagementScore(userId, startDate, endDate))
            .recommendations(generateRecommendations(userId))
            .build();
    }
    
    private double calculateEngagementScore(String userId, LocalDate startDate, LocalDate endDate) {
        // Composite score based on:
        // - Study consistency (30%)
        // - Group participation (25%)
        // - Session completion rate (20%)
        // - App usage frequency (15%)
        // - Social interactions (10%)
        
        double consistencyScore = calculateConsistencyScore(userId, startDate, endDate);
        double participationScore = calculateParticipationScore(userId, startDate, endDate);
        double completionScore = calculateCompletionScore(userId, startDate, endDate);
        double usageScore = calculateUsageScore(userId, startDate, endDate);
        double socialScore = calculateSocialScore(userId, startDate, endDate);
        
        return (consistencyScore * 0.3 + participationScore * 0.25 + 
                completionScore * 0.2 + usageScore * 0.15 + socialScore * 0.1);
    }
}
```

### Deployment & DevOps Strategy

#### Docker Configuration
```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim as build

WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests

FROM openjdk:17-jdk-slim

RUN groupadd -r studysync && useradd -r -g studysync studysync

VOLUME /tmp

ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

USER studysync:studysync

ENTRYPOINT ["java","-cp","app:app/lib/*","com.studysync.StudySyncApplication"]

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

#### Docker Compose for Development
```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - MONGODB_URI=mongodb://mongodb:27017/studysync
      - REDIS_URL=redis://redis:6379
    depends_on:
      - mongodb
      - redis
    networks:
      - studysync-network
    restart: unless-stopped
    
  mongodb:
    image: mongo:7.0
    ports:
      - "27017:27017"
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=password
      - MONGO_INITDB_DATABASE=studysync
    volumes:
      - mongodb_data:/data/db
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - studysync-network
    restart: unless-stopped
      
  redis:
    image: redis:7.2-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - studysync-network
    restart: unless-stopped
    command: redis-server --appendonly yes
    
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    networks:
      - studysync-network
    restart: unless-stopped

volumes:
  mongodb_data:
  redis_data:

networks:
  studysync-network:
    driver: bridge
```

#### GitHub Actions CI/CD
```yaml
# .github/workflows/deploy.yml
name: Build, Test, and Deploy

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: studysync/backend

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mongodb:
        image: mongo:7.0
        ports:
          - 27017:27017
      redis:
        image: redis:7.2
        ports:
          - 6379:6379
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        
    - name: Run tests
      run: ./mvnw clean verify
      
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: target/surefire-reports/
        
    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
      with:
        file: target/site/jacoco/jacoco.xml

  build-and-push:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Log in to Container Registry
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}
        
    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: |
          ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
          ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    environment: production
    
    steps:
    - name: Deploy to production
      uses: appleboy/ssh-action@v1.0.0
      with:
        host: ${{ secrets.PRODUCTION_HOST }}
        username: ${{ secrets.PRODUCTION_USER }}
        key: ${{ secrets.PRODUCTION_SSH_KEY }}
        script: |
          docker pull ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
          docker-compose -f /opt/studysync/docker-compose.prod.yml up -d
          docker system prune -f
```

#### Production Environment Configuration
```yaml
# application-prod.yml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}
      database: studysync
  redis:
    url: ${REDIS_URL}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 1
        
  security:
    jwt:
      secret: ${JWT_SECRET}
      access-token-expiration: 900000     # 15 minutes
      refresh-token-expiration: 2592000000 # 30 days
      
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      
logging:
  level:
    com.studysync: INFO
    org.springframework.security: WARN
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/studysync/application.log
    
server:
  port: 8080
  servlet:
    context-path: /api/v1
  error:
    include-stacktrace: never
    include-message: always
```

### Performance Optimization Strategy

#### Database Query Optimization
```java
@Repository
public class CustomSessionRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // Optimized aggregation for weekly leaderboards
    public List<LeaderboardEntry> getWeeklyLeaderboard(String groupId, LocalDate weekStart, LocalDate weekEnd) {
        Aggregation aggregation = Aggregation.newAggregation(
            // Stage 1: Match sessions in date range and group
            Aggregation.match(Criteria.where("groupId").is(groupId)
                .and("startTime").gte(weekStart).lt(weekEnd)
                .and("status").is("completed")),
                
            // Stage 2: Group by user and sum duration
            Aggregation.group("userId")
                .sum("durationSeconds").as("totalSeconds")
                .count().as("sessionCount")
                .avg("durationSeconds").as("averageDuration")
                .max("durationSeconds").as("longestSession"),
                
            // Stage 3: Lookup user details
            Aggregation.lookup("users", "_id", "_id", "userDetails"),
            
            // Stage 4: Project final structure
            Aggregation.project()
                .and("_id").as("userId")
                .and("totalSeconds").as("totalSeconds")
                .and("sessionCount").as("sessionCount")
                .and("averageDuration").as("averageDuration")
                .and("longestSession").as("longestSession")
                .and("userDetails.displayName").as("displayName")
                .and("userDetails.avatarUrl").as("avatarUrl"),
                
            // Stage 5: Sort by total time descending
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalSeconds")),
            
            // Stage 6: Limit to top 50
            Aggregation.limit(50)
        );
        
        return mongoTemplate.aggregate(aggregation, "sessions", LeaderboardEntry.class)
            .getMappedResults();
    }
    
    // Efficient user session history with pagination
    public Page<Session> getUserSessionHistory(String userId, Pageable pageable, 
                                             LocalDate startDate, LocalDate endDate) {
        Query query = new Query()
            .addCriteria(Criteria.where("userId").is(userId)
                .and("startTime").gte(startDate).lt(endDate)
                .and("status").is("completed"))
            .with(pageable)
            .with(Sort.by(Sort.Direction.DESC, "startTime"));
            
        List<Session> sessions = mongoTemplate.find(query, Session.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Session.class);
        
        return PageableExecutionUtils.getPage(sessions, pageable, () -> total);
    }
}
```

#### Connection Pool Optimization
```yaml
# MongoDB connection configuration
spring:
  data:
    mongodb:
      uri: mongodb://user:pass@host:27017/studysync?maxPoolSize=20&minPoolSize=5&maxIdleTimeMS=300000
      
# Redis connection pool
spring:
  redis:
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 2000ms
      shutdown-timeout: 100ms
```

## API Documentation

### Enhanced REST API Specification

#### Authentication Endpoints
```yaml
paths:
  /auth/register:
    post:
      summary: Register new user account
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [email, password, displayName, timezone]
              properties:
                email:
                  type: string
                  format: email
                  example: "student@university.edu"
                password:
                  type: string
                  minLength: 8
                  pattern: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]"
                displayName:
                  type: string
                  minLength: 2
                  maxLength: 50
                  example: "John Smith"
                timezone:
                  type: string
                  example: "America/New_York"
                deviceInfo:
                  $ref: '#/components/schemas/DeviceInfo'
      responses:
        '201':
          description: User registered successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  user:
                    $ref: '#/components/schemas/User'
                  tokens:
                    $ref: '#/components/schemas/TokenPair'
        '400':
          description: Validation error
        '409':
          description: Email already exists

  /auth/login:
    post:
      summary: Authenticate user
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [email, password]
              properties:
                email:
                  type: string
                  format: email
                password:
                  type: string
                deviceInfo:
                  $ref: '#/components/schemas/DeviceInfo'
                rememberMe:
                  type: boolean
                  default: false
      responses:
        '200':
          description: Login successful
          content:
            application/json:
              schema:
                type: object
                properties:
                  user:
                    $ref: '#/components/schemas/User'
                  tokens:
                    $ref: '#/components/schemas/TokenPair'
                  requiresTwoFactor:
                    type: boolean
        '401':
          description: Invalid credentials
        '423':
          description: Account locked due to suspicious activity
```

#### Session Management Endpoints
```yaml
  /sessions/clock-in:
    post:
      summary: Start a new study session
      security:
        - bearerAuth: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                groupId:
                  type: string
                  nullable: true
                  description: "Group ID for group study session"
                studySubject:
                  type: string
                  maxLength: 100
                  example: "Computer Science"
                location:
                  type: string
                  maxLength: 100
                  example: "Library"
                plannedDuration:
                  type: integer
                  description: "Planned session duration in minutes"
                  minimum: 1
                  maximum: 480
      responses:
        '201':
          description: Session started successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ActiveSession'
        '400':
          description: Validation error or existing active session
        '404':
          description: Group not found or user not member

  /sessions/clock-out:
    post:
      summary: End current study session
      security:
        - bearerAuth: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                mood:
                  type: string
                  enum: [focused, distracted, tired, energetic]
                  example: "focused"
                notes:
                  type: string
                  maxLength: 500
                  example: "Completed algorithms homework"
                productivity:
                  type: integer
                  minimum: 1
                  maximum: 5
                  description: "Self-rated productivity (1-5)"
      responses:
        '200':
          description: Session ended successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CompletedSession'
        '400':
          description: No active session found
        '422':
          description: Session validation failed

  /sessions/active:
    get:
      summary: Get current active session
      security:
        - bearerAuth: []
      responses:
        '200':
          description: Active session details
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ActiveSession'
        '204':
          description: No active session
```

#### Advanced Leaderboard Endpoints
```yaml
  /leaderboard/weekly:
    get:
      summary: Get weekly leaderboard for group
      security:
        - bearerAuth: []
      parameters:
        - name: groupId
          in: query
          required: true
          schema:
            type: string
        - name: week
          in: query
          required: false
          schema:
            type: string
            pattern: "^\d{4}-W\d{2}$"
            example: "2025-W03"
          description: "ISO week format, defaults to current week"
        - name: limit
          in: query
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
        - name: includeInactive
          in: query
          schema:
            type: boolean
            default: false
      responses:
        '200':
          description: Weekly leaderboard data
          content:
            application/json:
              schema:
                type: object
                properties:
                  leaderboard:
                    type: array
                    items:
                      $ref: '#/components/schemas/LeaderboardEntry'
                  metadata:
                    type: object
                    properties:
                      week:
                        type: string
                        example: "2025-W03"
                      weekStart:
                        type: string
                        format: date-time
                      weekEnd:
                        type: string
                        format: date-time
                      totalParticipants:
                        type: integer
                      groupAverage:
                        type: number
                        description: "Average study time in seconds"
                      myRank:
                        type: integer
                        nullable: true
                      myEntry:
                        $ref: '#/components/schemas/LeaderboardEntry'

  /leaderboard/trends:
    get:
      summary: Get leaderboard trends over time
      security:
        - bearerAuth: []
      parameters:
        - name: groupId
          in: query
          required: true
          schema:
            type: string
        - name: weeks
          in: query
          schema:
            type: integer
            minimum: 2
            maximum: 12
            default: 4
          description: "Number of weeks to include in trend"
      responses:
        '200':
          description: Trend data
          content:
            application/json:
              schema:
                type: object
                properties:
                  trends:
                    type: array
                    items:
                      type: object
                      properties:
                        week:
                          type: string
                        myRank:
                          type: integer
                        myStudyTime:
                          type: integer
                        groupAverage:
                          type: number
                        participantCount:
                          type: integer
```

### Component Schemas
```yaml
components:
  schemas:
    User:
      type: object
      properties:
        id:
          type: string
        email:
          type: string
          format: email
        displayName:
          type: string
        avatarUrl:
          type: string
          format: uri
          nullable: true
        timezone:
          type: string
        preferences:
          $ref: '#/components/schemas/UserPreferences'
        analytics:
          $ref: '#/components/schemas/UserAnalytics'
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    ActiveSession:
      type: object
      properties:
        id:
          type: string
        userId:
          type: string
        groupId:
          type: string
          nullable: true
        startTime:
          type: string
          format: date-time
        currentDuration:
          type: integer
          description: "Current session duration in seconds"
        studySubject:
          type: string
          nullable: true
        location:
          type: string
          nullable: true
        plannedDuration:
          type: integer
          nullable: true

    CompletedSession:
      allOf:
        - $ref: '#/components/schemas/ActiveSession'
        - type: object
          properties:
            endTime:
              type: string
              format: date-time
            finalDuration:
              type: integer
            mood:
              type: string
              enum: [focused, distracted, tired, energetic]
            productivity:
              type: integer
              minimum: 1
              maximum: 5
            notes:
              type: string
            validation:
              $ref: '#/components/schemas/SessionValidation'

    LeaderboardEntry:
      type: object
      properties:
        userId:
          type: string
        displayName:
          type: string
        avatarUrl:
          type: string
          nullable: true
        totalSeconds:
          type: integer
        sessionCount:
          type: integer
        averageDuration:
          type: number
        longestSession:
          type: integer
        rank:
          type: integer
        streakDays:
          type: integer
        isCurrentUser:
          type: boolean
        badgeStatus:
          type: string
          enum: [none, bronze, silver, gold, diamond]

    DeviceInfo:
      type: object
      properties:
        platform:
          type: string
          enum: [android, ios, web]
        version:
          type: string
        model:
          type: string
        appVersion:
          type: string

    TokenPair:
      type: object
      properties:
        accessToken:
          type: string
        refreshToken:
          type: string
        expiresIn:
          type: integer
          description: "Access token expiration in seconds"

    SessionValidation:
      type: object
      properties:
        anomalyScore:
          type: number
          minimum: 0
          maximum: 1
        flags:
          type: array
          items:
            type: string
        isValid:
          type: boolean
        validatedAt:
          type: string
          format: date-time

  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

## Testing Strategy

### Comprehensive Testing Approach

#### Unit Testing
```java
@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    
    @Mock
    private SessionRepository sessionRepository;
    
    @Mock
    private AntiCheatService antiCheatService;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private SessionService sessionService;
    
    @Test
    @DisplayName("Should start session successfully for valid user")
    void shouldStartSessionSuccessfully() {
        // Given
        String userId = "user123";
        String groupId = "group456";
        User user = createValidUser(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.findActiveSessionByUserId(userId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Session session = sessionService.startSession(userId, groupId, null);
        
        // Then
        assertThat(session).isNotNull();
        assertThat(session.getUserId()).isEqualTo(userId);
        assertThat(session.getGroupId()).isEqualTo(groupId);
        assertThat(session.getStatus()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(session.getStartTime()).isCloseTo(Instant.now(), within(5, ChronoUnit.SECONDS));
        
        verify(sessionRepository).save(session);
        verify(antiCheatService, never()).validateSession(any());
    }
    
    @Test
    @DisplayName("Should throw exception when user already has active session")
    void shouldThrowExceptionForExistingActiveSession() {
        // Given
        String userId = "user123";
        Session existingSession = createActiveSession(userId);
        
        when(sessionRepository.findActiveSessionByUserId(userId))
            .thenReturn(Optional.of(existingSession));
        
        // When & Then
        assertThatThrownBy(() -> sessionService.startSession(userId, null, null))
            .isInstanceOf(ActiveSessionExistsException.class)
            .hasMessageContaining("User already has an active session");
            
        verify(sessionRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should validate session with anti-cheat on completion")
    void shouldValidateSessionOnCompletion() {
        // Given
        String sessionId = "session123";
        Session activeSession = createActiveSession("user123");
        ValidationResult validationResult = createValidValidationResult();
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(activeSession));
        when(antiCheatService.validateSession(activeSession)).thenReturn(validationResult);
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Session completedSession = sessionService.endSession(sessionId, "focused", "Good session");
        
        // Then
        assertThat(completedSession.getStatus()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(completedSession.getEndTime()).isNotNull();
        assertThat(completedSession.getDurationSeconds()).isPositive();
        assertThat(completedSession.getValidation()).isEqualTo(validationResult);
        
        verify(antiCheatService).validateSession(activeSession);
    }
}
```

#### Integration Testing with Testcontainers
```java
@SpringBootTest
@Testcontainers
class SessionControllerIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);
    
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }
    
    @Test
    @WithMockUser(username = "user123")
    void shouldCreateAndCompleteSessionEndToEnd() {
        // Given
        User user = createTestUser("user123");
        userRepository.save(user);
        
        SessionStartRequest startRequest = SessionStartRequest.builder()
            .groupId("group123")
            .studySubject("Computer Science")
            .build();
            
        // When - Start session
        ResponseEntity<SessionResponse> startResponse = restTemplate.postForEntity(
            "/api/v1/sessions/clock-in",
            startRequest,
            SessionResponse.class
        );
        
        // Then - Verify session started
        assertThat(startResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SessionResponse sessionResponse = startResponse.getBody();
        assertThat(sessionResponse).isNotNull();
        assertThat(sessionResponse.getId()).isNotBlank();
        assertThat(sessionResponse.getStatus()).isEqualTo("ACTIVE");
        
        // Wait a bit to ensure duration > 0
        Thread.sleep(1000);
        
        // When - End session
        SessionEndRequest endRequest = SessionEndRequest.builder()
            .mood("focused")
            .notes("Great session!")
            .productivity(5)
            .build();
            
        ResponseEntity<SessionResponse> endResponse = restTemplate.postForEntity(
            "/api/v1/sessions/clock-out",
            endRequest,
            SessionResponse.class
        );
        
        // Then - Verify session completed
        assertThat(endResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SessionResponse completedSession = endResponse.getBody();
        assertThat(completedSession.getStatus()).isEqualTo("COMPLETED");
        assertThat(completedSession.getDurationSeconds()).isPositive();
        
        // Verify database state
        Optional<Session> dbSession = sessionRepository.findById(sessionResponse.getId());
        assertThat(dbSession).isPresent();
        assertThat(dbSession.get().getStatus()).isEqualTo(SessionStatus.COMPLETED);
    }
}
```

#### Mobile App Testing (React Native)
```typescript
// __tests__/components/StudyTimer.test.tsx
import React from 'react';
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import StudyTimer from '../src/components/StudyTimer';
import { SessionService } from '../src/services/api/SessionService';

// Mock the session service
jest.mock('../src/services/api/SessionService');
const mockSessionService = SessionService as jest.Mocked<typeof SessionService>;

describe('StudyTimer Component', () => {
  let queryClient: QueryClient;
  
  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
    jest.clearAllMocks();
  });
  
  const renderComponent = (props = {}) => {
    return render(
      <QueryClientProvider client={queryClient}>
        <StudyTimer {...props} />
      </QueryClientProvider>
    );
  };
  
  it('should start session when clock-in button pressed', async () => {
    // Given
    const mockSession = {
      id: 'session123',
      status: 'ACTIVE',
      startTime: new Date().toISOString(),
      currentDuration: 0,
    };
    
    mockSessionService.startSession.mockResolvedValue(mockSession);
    
    const { getByTestId } = renderComponent();
    
    // When
    fireEvent.press(getByTestId('clock-in-button'));
    
    // Then
    await waitFor(() => {
      expect(mockSessionService.startSession).toHaveBeenCalledWith({
        groupId: null,
        studySubject: null,
      });
    });
    
    expect(getByTestId('timer-display')).toBeTruthy();
    expect(getByTestId('clock-out-button')).toBeTruthy();
  });
  
  it('should handle offline session start gracefully', async () => {
    // Given
    mockSessionService.startSession.mockRejectedValue(new Error('Network Error'));
    
    const { getByTestId, getByText } = renderComponent();
    
    // When
    fireEvent.press(getByTestId('clock-in-button'));
    
    // Then
    await waitFor(() => {
      expect(getByText('Session saved offline')).toBeTruthy();
      expect(getByTestId('offline-indicator')).toBeTruthy();
    });
  });
  
  it('should sync offline sessions when connection restored', async () => {
    // Given
    const mockOfflineSession = {
      id: 'offline_session_123',
      startTime: new Date().toISOString(),
      status: 'OFFLINE_PENDING',
    };
    
    // Simulate offline session exists
    const mockSyncService = require('../src/services/OfflineSyncService');
    mockSyncService.getPendingSessions.mockReturnValue([mockOfflineSession]);
    mockSyncService.syncSession.mockResolvedValue({ success: true });
    
    const { getByTestId } = renderComponent();
    
    // When - Simulate network restoration
    fireEvent(getByTestId('app-container'), 'networkStateChange', {
      isConnected: true,
    });
    
    // Then
    await waitFor(() => {
      expect(mockSyncService.syncSession).toHaveBeenCalledWith(mockOfflineSession);
    });
  });
});
```

#### Load Testing
```javascript
// k6-load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '2m', target: 100 },   // Ramp up to 100 users
    { duration: '5m', target: 100 },   // Stay at 100 users
    { duration: '2m', target: 200 },   // Ramp up to 200 users
    { duration: '5m', target: 200 },   // Stay at 200 users
    { duration: '2m', target: 0 },     // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests must be below 500ms
    http_req_failed: ['rate<0.02'],   // Error rate must be below 2%
  },
};

const BASE_URL = __ENV.BASE_URL || 'https://api.studysync.app';

export function setup() {
  // Create test users and get auth tokens
  const users = [];
  for (let i = 0; i < 10; i++) {
    const response = http.post(`${BASE_URL}/auth/register`, {
      email: `loadtest${i}@example.com`,
      password: 'LoadTest123!',
      displayName: `Load Test User ${i}`,
      timezone: 'America/New_York',
    });
    
    if (response.status === 201) {
      users.push(JSON.parse(response.body));
    }
  }
  
  return { users };
}

export default function (data) {
  const user = data.users[Math.floor(Math.random() * data.users.length)];
  const headers = {
    'Authorization': `Bearer ${user.tokens.accessToken}`,
    'Content-Type': 'application/json',
  };
  
  // Test session start
  let response = http.post(`${BASE_URL}/sessions/clock-in`, 
    JSON.stringify({ groupId: null }), 
    { headers }
  );
  
  check(response, {
    'session start status is 201': (r) => r.status === 201,
    'session start duration < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);
  
  const sessionId = JSON.parse(response.body).id;
  
  // Simulate study time
  sleep(Math.random() * 10 + 5); // 5-15 seconds
  
  // Test session end
  response = http.post(`${BASE_URL}/sessions/clock-out`, 
    JSON.stringify({ 
      mood: 'focused',
      notes: 'Load test session',
      productivity: 4 
    }), 
    { headers }
  );
  
  check(response, {
    'session end status is 200': (r) => r.status === 200,
    'session end duration < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);
  
  // Test leaderboard fetch
  response = http.get(`${BASE_URL}/leaderboard/weekly?groupId=default`, 
    { headers }
  );
  
  check(response, {
    'leaderboard status is 200': (r) => r.status === 200,
    'leaderboard duration < 200ms': (r) => r.timings.duration < 200,
  }) || errorRate.add(1);
  
  sleep(1);
}

export function teardown(data) {
  // Clean up test data
  data.users.forEach(user => {
    http.del(`${BASE_URL}/users/me`, {
      headers: { 'Authorization': `Bearer ${user.tokens.accessToken}` }
    });
  });
}
```

This enhanced technical plan addresses all the gaps identified in the original plan and provides a production-ready architecture for the StudySync application. The plan includes comprehensive security measures, performance optimizations, monitoring systems, and detailed implementation guidelines that will ensure a robust and scalable social study tracking platform.

<function_calls>
<invoke name="todo_write">
<parameter name="merge">true



