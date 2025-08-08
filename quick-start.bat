@echo off
echo ========================================
echo   StudySync - Quick Start
echo ========================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Java not found. Please install Java 17 first:
    echo https://adoptium.net/
    pause
    exit /b 1
)

echo [OK] Java is available
echo.

echo Starting simple StudySync demo...
echo.

cd backend

echo Building simple version...
copy pom-simple.xml pom.xml

echo.
echo Starting the application...
echo This will take a moment on first run...
echo.

.\mvnw.cmd spring-boot:run -Dspring-boot.run.main-class=com.studysync.SimpleStudySyncApplication

pause


