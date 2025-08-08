@echo off
echo ========================================
echo   StudySync - Local API Test
echo ========================================
echo.

echo Compiling and starting StudySync API...
javac -cp . StudySyncSimpleAPI.java

if %errorlevel% neq 0 (
    echo Failed to compile. Check for errors above.
    pause
    exit /b 1
)

echo.
echo Starting StudySync API Server...
echo.

start /B java StudySyncSimpleAPI

timeout /t 3 /nobreak >nul

echo ========================================
echo   StudySync API is now running!
echo ========================================
echo.
echo Test these URLs in your browser:
echo.
echo   http://localhost:8080/                    - Main API
echo   http://localhost:8080/health              - Health Check  
echo   http://localhost:8080/api/v1/stats        - Statistics
echo   http://localhost:8080/api/v1/sessions/active - Active Sessions
echo.
echo To test session management:
echo   curl -X POST http://localhost:8080/api/v1/sessions/start
echo   curl -X POST http://localhost:8080/api/v1/sessions/end
echo.
echo Press Ctrl+C in this window to stop the server
echo.
pause

