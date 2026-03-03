@echo off
echo ============================================
echo Maven Auto Installer for Windows
echo ============================================
echo.

REM Check if Maven is already installed
where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo Maven is already installed!
    mvn -version
    echo.
    pause
    exit /b 0
)

echo Maven not found. Starting installation...
echo.

REM Check if running as Administrator
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Not running as Administrator.
    echo Some steps may require admin privileges.
    echo.
)

set MAVEN_VERSION=3.9.9
set INSTALL_DIR=C:\maven
set MAVEN_DIR=%INSTALL_DIR%\apache-maven-%MAVEN_VERSION%
set DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set ZIP_FILE=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip

echo Maven Version: %MAVEN_VERSION%
echo Install Directory: %INSTALL_DIR%
echo.

echo Step 1: Creating installation directory...
if not exist "%INSTALL_DIR%" (
    mkdir "%INSTALL_DIR%"
    if %errorlevel% neq 0 (
        echo ERROR: Failed to create directory. Try running as Administrator.
        pause
        exit /b 1
    )
)
echo Done!
echo.

echo Step 2: Downloading Maven...
echo This may take a few minutes...
echo Download URL: %DOWNLOAD_URL%
echo.

REM Use PowerShell to download
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%ZIP_FILE%'}"

if %errorlevel% neq 0 (
    echo ERROR: Download failed!
    echo Please download manually from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

if not exist "%ZIP_FILE%" (
    echo ERROR: Downloaded file not found!
    pause
    exit /b 1
)

echo Download completed!
echo.

echo Step 3: Extracting Maven...
powershell -Command "& {Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%INSTALL_DIR%' -Force}"

if %errorlevel% neq 0 (
    echo ERROR: Extraction failed!
    pause
    exit /b 1
)

echo Extraction completed!
echo.

echo Step 4: Setting up Environment Variables...
echo.

REM Add Maven to System PATH using PowerShell
echo Adding Maven to PATH...
powershell -Command "& {$oldPath = [Environment]::GetEnvironmentVariable('Path', 'Machine'); if ($oldPath -notlike '*%MAVEN_DIR%\bin*') { $newPath = $oldPath + ';%MAVEN_DIR%\bin'; [Environment]::SetEnvironmentVariable('Path', $newPath, 'Machine'); Write-Host 'PATH updated successfully'; } else { Write-Host 'Maven already in PATH'; }}"

if %errorlevel% neq 0 (
    echo.
    echo WARNING: Automatic PATH update failed.
    echo Please add this manually to your System PATH:
    echo %MAVEN_DIR%\bin
    echo.
    echo Manual steps:
    echo 1. Press Win + R, type: sysdm.cpl
    echo 2. Go to Advanced tab
    echo 3. Click Environment Variables
    echo 4. Under System Variables, select Path and click Edit
    echo 5. Click New and add: %MAVEN_DIR%\bin
    echo 6. Click OK on all dialogs
    echo.
    pause
) else (
    echo PATH updated successfully!
    echo.
)

echo Step 5: Cleaning up...
del "%ZIP_FILE%" >nul 2>&1
echo Done!
echo.

echo ============================================
echo Installation Complete!
echo ============================================
echo.
echo Maven has been installed to: %MAVEN_DIR%
echo.
echo IMPORTANT: Please RESTART this terminal (close and reopen)
echo Then run: mvn -version
echo.
echo After restart, you can run:
echo   cd "c:\dnw\incubationPayMedia\crm test 2\backend"
echo   mvn spring-boot:run
echo.

pause
