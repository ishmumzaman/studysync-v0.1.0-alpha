@echo off
echo ========================================
echo   StudySync - Local Development Setup
echo ========================================
echo.
echo This script will help you run StudySync locally without Docker
echo.

REM Create a simple in-memory setup for development
echo Creating local development environment...
echo.

REM Check if backend directory exists
if not exist "backend" (
    echo [!] Backend directory not found. Creating project structure...
    mkdir backend\src\main\java\com\studysync
    mkdir backend\src\main\resources
    mkdir mobile\src
)

echo ========================================
echo   Option 1: Full Setup (Recommended)
echo ========================================
echo.
echo 1. Install these tools first:
echo    - Java 17+: https://adoptium.net/
echo    - Node.js 18+: https://nodejs.org/
echo    - MongoDB: https://www.mongodb.com/try/download/community
echo    - Redis: https://github.com/microsoftarchive/redis/releases
echo.
echo 2. Start MongoDB and Redis locally
echo.
echo 3. Run the backend:
echo    cd backend
echo    mvn spring-boot:run
echo.
echo 4. Run the mobile app:
echo    cd mobile
echo    npm install
echo    npm start
echo.
echo ========================================
echo   Option 2: Quick Demo (No Database)
echo ========================================
echo.
echo For a quick demo without database setup:
echo.
echo 1. The backend will use H2 in-memory database
echo 2. Run: cd backend ^&^& mvn spring-boot:run -Dspring.profiles.active=demo
echo.
pause



