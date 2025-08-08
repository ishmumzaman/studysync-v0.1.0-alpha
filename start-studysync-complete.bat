@echo off
echo ========================================
echo   StudySync - Complete Startup
echo ========================================
echo.

echo 🔍 Checking if Docker is running...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo ✅ Docker is available.
echo.

echo 🚀 Starting StudySync services...
docker-compose -f docker-compose-simple.yml up -d

echo.
echo ⏳ Waiting for services to fully start...
echo    This may take 30-60 seconds...

:wait_loop
timeout /t 5 /nobreak >nul
echo    🔄 Checking API status...

powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/health' -TimeoutSec 5; if ($response.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1

if errorlevel 1 (
    echo    ⏳ Still starting...
    goto wait_loop
)

echo.
echo ✅ StudySync API is ready!
echo.

echo 📊 Current service status:
docker-compose -f docker-compose-simple.yml ps

echo.
echo 🧪 Testing API endpoints...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/v1/stats'; Write-Host '✅ Stats API: Working' -ForegroundColor Green } catch { Write-Host '❌ Stats API: Failed' -ForegroundColor Red }"

echo.
echo 🌐 Opening StudySync Web App...
start StudySync-WebApp.html

echo.
echo ========================================
echo 🎉 StudySync is ready to use!
echo.
echo Available URLs:
echo   📱 Web App: StudySync-WebApp.html (just opened)
echo   🔗 API: http://localhost:8080
echo   🗄️ MongoDB Admin: http://localhost:8081
echo.
echo 💡 TIP: Keep this window open to see any important messages.
echo      To stop StudySync, run: docker-compose -f docker-compose-simple.yml down
echo ========================================
echo.

echo Press any key to open command menu, or close this window to keep StudySync running...
pause >nul

echo.
echo Opening StudySync command menu...
call studysync-commands.bat

