# StudySync - Social Study Tracking Application

A comprehensive social study tracking mobile application that gamifies learning through competitive time tracking, social accountability, and intelligent engagement systems.

## 🚀 Features

- **Authenticated Study Session Tracking**: Server-authoritative timing with anti-cheat mechanisms
- **Social Study Groups**: Create and join study groups with friends
- **Real-time Leaderboards**: Compete with friends on weekly study time leaderboards
- **Offline-First Architecture**: Full functionality even without internet connection
- **Intelligent Notifications**: Personalized study reminders based on your patterns
- **Privacy-First Design**: Granular privacy controls and GDPR/CCPA compliance

## 🏗️ Architecture Overview

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2+ with Java 17
- **Database**: MongoDB for data persistence
- **Cache**: Redis for high-performance caching
- **Security**: JWT-based authentication with device fingerprinting
- **Real-time**: WebSocket support for live updates
- **API**: RESTful API with OpenAPI documentation

### Mobile App (React Native)
- **Framework**: React Native 0.73+ with TypeScript
- **State Management**: Zustand + React Query
- **UI Library**: React Native Paper (Material Design 3)
- **Navigation**: React Navigation 6
- **Storage**: Secure Store for sensitive data, AsyncStorage for general data

## 🛠️ Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Docker and Docker Compose
- MongoDB 7.0
- Redis 7.2
- Maven 3.9+

## 📦 Installation

### Backend Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/studysync.git
cd studysync
```

2. Build the backend:
```bash
cd backend
mvn clean install
```

3. Run with Docker Compose:
```bash
docker-compose up -d
```

The backend will be available at `http://localhost:8080/api/v1`

### Mobile App Setup

1. Navigate to mobile directory:
```bash
cd mobile
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

4. Run on your device:
- iOS: `npm run ios`
- Android: `npm run android`

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the backend directory:

```env
MONGODB_URI=mongodb://localhost:27017/studysync
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret-key
```

### Mobile App Configuration

Update `mobile/src/config/constants.ts`:

```typescript
export const API_BASE_URL = 'http://localhost:8080/api/v1';
```

## 🚀 Deployment

### Production Deployment with Docker

1. Build the production image:
```bash
docker build -t studysync-backend:latest .
```

2. Run with production configuration:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Mobile App Deployment

#### Android
```bash
cd mobile
expo build:android
```

#### iOS
```bash
cd mobile
expo build:ios
```

## 📊 API Documentation

API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - Logout user

#### Sessions
- `POST /api/v1/sessions/clock-in` - Start study session
- `POST /api/v1/sessions/clock-out` - End study session
- `GET /api/v1/sessions/active` - Get active session
- `GET /api/v1/sessions` - Get session history

#### Groups
- `POST /api/v1/groups` - Create group
- `GET /api/v1/groups` - List user's groups
- `POST /api/v1/groups/join` - Join group with invite code

#### Leaderboard
- `GET /api/v1/leaderboard/weekly` - Get weekly leaderboard
- `GET /api/v1/leaderboard/trends` - Get leaderboard trends

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Mobile App Tests
```bash
cd mobile
npm test
```

### Load Testing
```bash
k6 run tests/load/k6-load-test.js
```

## 📈 Monitoring

- Health Check: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`

## 🔒 Security Features

- JWT token rotation with refresh tokens
- Device fingerprinting for session validation
- Rate limiting with Bucket4j
- BCrypt password hashing
- CORS protection
- SQL injection prevention through parameterized queries
- XSS protection through input validation

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏗️ Project Structure

```
StudySync/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/studysync/
│   │   │   │   ├── config/      # Configuration classes
│   │   │   │   ├── controller/  # REST controllers
│   │   │   │   ├── service/     # Business logic
│   │   │   │   ├── repository/  # Data access
│   │   │   │   ├── model/       # Domain models
│   │   │   │   ├── dto/         # Data transfer objects
│   │   │   │   ├── security/    # Security components
│   │   │   │   └── exception/   # Exception handling
│   │   │   └── resources/       # Configuration files
│   │   └── test/                # Test files
│   └── pom.xml                  # Maven configuration
├── mobile/                  # React Native app
│   ├── src/
│   │   ├── components/          # Reusable components
│   │   ├── screens/            # Screen components
│   │   ├── services/           # API services
│   │   ├── contexts/           # React contexts
│   │   ├── hooks/              # Custom hooks
│   │   ├── navigation/         # Navigation setup
│   │   └── utils/              # Utility functions
│   ├── App.tsx                 # App entry point
│   └── package.json            # Node dependencies
├── docker-compose.yml          # Docker orchestration
├── Dockerfile                  # Container definition
└── README.md                   # This file
```

## 🚦 Development Workflow

1. **Local Development**: Use Docker Compose for backend services
2. **Code Style**: Follow Google Java Style Guide for backend, Airbnb for React Native
3. **Branch Strategy**: GitFlow with main, develop, and feature branches
4. **CI/CD**: GitHub Actions for automated testing and deployment

## 📞 Support

For support, email ishmumz07@gmail.com

## 🎯 Roadmap

- [ ] Web dashboard for detailed analytics
- [ ] Apple Watch and WearOS companion apps
- [ ] AI-powered study recommendations
- [ ] Calendar integration
- [ ] Pomodoro timer mode
- [ ] Study streaks and achievements
- [ ] Export study data to CSV/PDF

## ⚡ Performance

- API Response Times: P95 < 200ms
- Mobile App Cold Start: < 2 seconds
- WebSocket Latency: < 100ms
- Offline Sync Recovery: < 5 seconds

## 🙏 Contributors 
- [Ishmum Zaman](github.com/ishmumzaman)
- Mohammed Nihad

## 🙏 Acknowledgments

- Spring Boot community
- React Native community
- MongoDB and Redis teams
- All contributors and testers

---

Built with ❤️ by the StudySync Team



