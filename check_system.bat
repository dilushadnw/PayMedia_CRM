@echo off
echo ============================================
echo PayMedia CRM - System Check
echo ============================================
echo.
echo Checking if all required software is installed...
echo.

set ALL_OK=1

REM Check Java
echo [1/3] Checking Java JDK...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Java is installed
    java -version 2>&1 | findstr /C:"version"
) else (
    echo ✗ Java NOT found!
    echo    Download from: https://adoptium.net/
    set ALL_OK=0
)
echo.

REM Check Maven
echo [2/3] Checking Apache Maven...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Maven is installed
    mvn -version 2>&1 | findstr /C:"Apache Maven"
) else (
    echo ✗ Maven NOT found!
    echo    Download from: https://maven.apache.org/download.cgi
    echo    See: INSTALLATION_GUIDE.txt for setup instructions
    set ALL_OK=0
)
echo.

REM Check Node.js
echo [3/3] Checking Node.js and npm...
node -v >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Node.js is installed
    node -v
    npm -v 2>&1
) else (
    echo ✗ Node.js NOT found!
    echo    Download from: https://nodejs.org/
    set ALL_OK=0
)
echo.

echo ============================================
if %ALL_OK% equ 1 (
    echo ✓✓✓ ALL SYSTEMS READY! ✓✓✓
    echo.
    echo You can now run the application:
    echo.
    echo Backend:  cd backend  ^&^&  start_backend.bat
    echo Frontend: cd frontend ^&^&  start_frontend.bat
) else (
    echo ✗✗✗ SOME SOFTWARE IS MISSING ✗✗✗
    echo.
    echo Please install the missing software.
    echo See: INSTALLATION_GUIDE.txt for detailed instructions
)
echo ============================================
echo.

pause
