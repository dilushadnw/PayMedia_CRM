@echo off
echo ============================================
echo PayMedia CRM - Backend Starter
echo ============================================
echo.
echo Starting Spring Boot Application...
echo This may take a few minutes on first run...
echo.

cd /d "%~dp0"

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven not found!
    echo.
    echo Please install Maven from: https://maven.apache.org/download.cgi
    echo Or use the Maven Wrapper: mvnw.cmd spring-boot:run
    echo.
    pause
    exit /b 1
)

REM Check Java version
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found!
    echo.
    echo Please install Java JDK 11 or higher
    echo Download from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo Java and Maven found!
echo.
echo Database: H2 In-Memory (No MySQL needed!)
echo API will be available at: http://localhost:8080/api/organisations
echo H2 Console: http://localhost:8080/h2-console
echo.

REM Run Spring Boot
mvn spring-boot:run

pause
