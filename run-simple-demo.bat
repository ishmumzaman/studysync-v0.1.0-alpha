@echo off
echo ========================================
echo   StudySync - Simple Demo Launcher
echo ========================================
echo.

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Java is not installed or not in PATH
    echo.
    echo Please install Java 17 or higher:
    echo https://adoptium.net/
    echo.
    echo After installation, restart this script.
    pause
    exit /b 1
)

echo [OK] Java is available
echo.

echo Starting MongoDB and Redis with Docker...
docker-compose up -d mongodb redis

echo.
echo Waiting for databases to start...
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo   Demo is Ready!
echo ========================================
echo.
echo [!] Due to compilation issues in the full codebase,
echo     here's how to run a quick demo:
echo.
echo Option 1 - API Testing:
echo   1. Open http://localhost:27017 (MongoDB)
echo   2. Open http://localhost:6379 (Redis - use redis-cli)
echo.
echo Option 2 - Use the simplified version:
echo   The databases are running. You can now:
echo   - Connect to MongoDB on localhost:27017
echo   - Connect to Redis on localhost:6379
echo.
echo MongoDB connection string:
echo   mongodb://admin:password@localhost:27017/studysync?authSource=admin
echo.
echo To stop: docker-compose down
echo.
pause


