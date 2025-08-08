@echo off
echo ========================================
echo   StudySync - Restart Script
echo ========================================
echo.
echo This will restart StudySync to fix any connection issues.
echo.
set /p restart="Do you want to restart StudySync? (y/n): "

if /i "%restart%" neq "y" goto exit

echo.
echo 🔄 Stopping StudySync containers...
docker-compose -f docker-compose-simple.yml down

echo.
echo 🧹 Cleaning up any remaining containers...
docker container prune -f

echo.
echo 🚀 Starting StudySync...
docker-compose -f docker-compose-simple.yml up -d

echo.
echo ⏳ Waiting for services to start...
timeout /t 10 /nobreak > nul

echo.
echo 🔍 Checking service status...
docker-compose -f docker-compose-simple.yml ps

echo.
echo 🌐 Testing API connection...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/health'; Write-Host '✅ StudySync is running!' -ForegroundColor Green } catch { Write-Host '❌ StudySync is not responding' -ForegroundColor Red }"

echo.
echo 📱 Opening StudySync Web App...
start StudySync-WebApp.html

goto end

:exit
echo.
echo Operation cancelled.

:end
echo.
echo ========================================
pause

