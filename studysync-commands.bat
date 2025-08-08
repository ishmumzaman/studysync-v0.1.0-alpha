@echo off
goto main_loop

:start_session
echo.
echo Starting study session...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/v1/sessions/start' -Method POST; $content = $response.Content | ConvertFrom-Json; Write-Host '✅ Session started successfully!' -ForegroundColor Green; Write-Host 'Session ID:' $content.sessionId; Write-Host 'Start Time:' $content.startTime } catch { Write-Host '❌ Failed to start session' -ForegroundColor Red; Write-Host $_.Exception.Message }"
goto menu

:end_session
echo.
echo Ending study session...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/v1/sessions/end' -Method POST; $content = $response.Content | ConvertFrom-Json; Write-Host '✅ Session ended successfully!' -ForegroundColor Green; Write-Host 'Session ID:' $content.sessionId; Write-Host 'Duration:' $content.duration } catch { Write-Host '❌ Failed to end session' -ForegroundColor Red; Write-Host $_.Exception.Message }"
goto menu

:check_active
echo.
echo Checking active sessions...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/v1/sessions/active'; $content = $response.Content | ConvertFrom-Json; Write-Host '📚 Active Sessions:' -ForegroundColor Blue; if ($content.activeSessions.Count -eq 0) { Write-Host 'No active sessions' } else { $content.activeSessions | ForEach-Object { Write-Host 'ID:' $_.id 'Started:' $_.startTime } } } catch { Write-Host '❌ Failed to check sessions' -ForegroundColor Red }"
goto menu

:view_stats
echo.
echo Viewing statistics...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/v1/stats'; $content = $response.Content | ConvertFrom-Json; Write-Host '📊 StudySync Statistics:' -ForegroundColor Blue; Write-Host 'Total Sessions:' $content.totalSessions; Write-Host 'Active Sessions:' $content.activeSessions; Write-Host 'Completed Sessions:' $content.completedSessions; Write-Host 'Version:' $content.version } catch { Write-Host '❌ Failed to get stats' -ForegroundColor Red }"
goto menu

:open_webapp
echo.
echo Opening StudySync Web App...
start StudySync-WebApp.html
goto menu

:open_mongo
echo.
echo Opening MongoDB Admin...
start http://localhost:8081
goto menu

:menu
echo.
echo ========================================
pause
cls
goto :main_loop

:main_loop
echo ========================================
echo   StudySync - Quick Commands
echo ========================================
echo.
echo Choose an action:
echo.
echo 1. Start Study Session
echo 2. End Study Session
echo 3. Check Active Sessions
echo 4. View Statistics
echo 5. Open Web App
echo 6. Open MongoDB Admin
echo 7. Exit
echo.
set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto start_session
if "%choice%"=="2" goto end_session
if "%choice%"=="3" goto check_active
if "%choice%"=="4" goto view_stats
if "%choice%"=="5" goto open_webapp
if "%choice%"=="6" goto open_mongo
if "%choice%"=="7" goto exit
goto main_loop

:exit
echo.
echo Thanks for using StudySync! Keep studying! 📚
echo.
pause
