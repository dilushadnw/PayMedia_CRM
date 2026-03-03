@echo off
echo ============================================
echo PayMedia CRM - Frontend Starter
echo ============================================
echo.

cd /d "%~dp0"

REM Check if Node.js is installed
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Node.js not found!
    echo.
    echo Please install Node.js from: https://nodejs.org/
    echo Recommended: LTS version
    echo.
    pause
    exit /b 1
)

REM Check if npm is installed
where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: npm not found!
    echo.
    echo Please install Node.js (includes npm) from: https://nodejs.org/
    echo.
    pause
    exit /b 1
)

echo Node.js and npm found!
echo.

REM Check if node_modules exists
if not exist "node_modules\" (
    echo Installing dependencies... This will take a few minutes...
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo.
        echo ERROR: Failed to install dependencies!
        echo.
        pause
        exit /b 1
    )
    echo.
    echo Dependencies installed successfully!
    echo.
)

echo Starting React development server...
echo.
echo Frontend will open at: http://localhost:3000
echo.
echo Press Ctrl+C to stop the server
echo.

call npm start

pause
