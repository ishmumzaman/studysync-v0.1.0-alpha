@echo off
echo ========================================
echo   StudySync - Quick Start for Windows
echo ========================================
echo.

echo Checking prerequisites...
echo.

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Java is NOT installed
    echo     Please install Java 17 or higher from:
    echo     https://adoptium.net/
    echo.
) else (
    echo [OK] Java is installed
)

REM Check Node.js
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Node.js is NOT installed
    echo     Please install Node.js 18 or higher from:
    echo     https://nodejs.org/
    echo.
) else (
    echo [OK] Node.js is installed
)

REM Check Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Docker is NOT installed
    echo     Please install Docker Desktop from:
    echo     https://www.docker.com/products/docker-desktop/
    echo.
) else (
    echo [OK] Docker is installed
)

echo.
echo ========================================
echo   Installation Steps
echo ========================================
echo.
echo Since this is your first run, please follow these steps:
echo.
echo 1. INSTALL PREREQUISITES:
echo    - Java 17: https://adoptium.net/
echo    - Node.js 18: https://nodejs.org/
echo    - Docker Desktop: https://www.docker.com/products/docker-desktop/
echo.
echo 2. AFTER INSTALLING, run this script again
echo.
echo 3. OR run manually without Docker:
echo    - Backend: cd backend && mvn spring-boot:run
echo    - Mobile: cd mobile && npm install && npm start
echo.
pause



