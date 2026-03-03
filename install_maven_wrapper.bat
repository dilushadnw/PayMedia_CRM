@echo off
REM ============================================
REM PayMedia CRM - Maven එක නැතිව Run කරන්න
REM H2 Database use කරන නිසා Maven install කරන්නත් එපා!
REM ============================================

echo ============================================
echo PayMedia CRM - Maven Wrapper Installer
echo ============================================
echo.

cd /d "%~dp0backend"

if exist "mvnw.cmd" (
    echo Maven Wrapper already exists!
    echo.
    echo You can now run the backend without installing Maven:
    echo   cd backend
    echo   mvnw.cmd spring-boot:run
    echo.
    pause
    exit /b 0
)

echo Installing Maven Wrapper...
echo This allows you to run Maven without installing it!
echo.

REM Check if we have internet connection
ping -n 1 google.com >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: No internet connection detected.
    echo Maven Wrapper needs internet for first-time setup.
    pause
    exit /b 1
)

REM Download maven wrapper files
echo Downloading Maven Wrapper files...
echo.

REM Create .mvn directory
if not exist ".mvn\wrapper" mkdir ".mvn\wrapper"

REM Download wrapper jar
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '.mvn/wrapper/maven-wrapper.jar'}"

REM Download wrapper properties
powershell -Command "& {'distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' | Out-File -FilePath '.mvn/wrapper/maven-wrapper.properties' -Encoding ASCII}"

REM Download mvnw.cmd
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/apache/maven-wrapper/master/mvnw.cmd' -OutFile 'mvnw.cmd'}"

if exist "mvnw.cmd" (
    echo.
    echo ============================================
    echo Maven Wrapper Installed Successfully!
    echo ============================================
    echo.
    echo You can now run the backend WITHOUT installing Maven:
    echo.
    echo   cd "c:\dnw\incubationPayMedia\crm test 2\backend"
    echo   mvnw.cmd spring-boot:run
    echo.
    echo The wrapper will automatically download Maven on first run.
    echo.
) else (
    echo.
    echo ERROR: Maven Wrapper installation failed!
    echo.
    echo Please try the full Maven installation instead:
    echo   install_maven.bat
    echo.
)

pause
