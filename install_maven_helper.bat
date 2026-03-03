@echo off
echo ============================================
echo Maven Quick Install Helper
echo ============================================
echo.
echo Your PowerShell has script restrictions.
echo Let's install Maven manually - it's EASY!
echo.
echo ============================================
echo OPTION 1: Download Maven Manually
echo ============================================
echo.
echo 1. Open browser and go to:
echo    https://maven.apache.org/download.cgi
echo.
echo 2. Download: apache-maven-3.9.9-bin.zip
echo.
echo 3. Extract to: C:\maven
echo.
echo 4. Add to System PATH:
echo    C:\maven\apache-maven-3.9.9\bin
echo.
echo See MAVEN_MANUAL_INSTALL.txt for detailed steps!
echo.
echo ============================================
echo OPTION 2: Use Chocolatey (if installed)
echo ============================================
echo.
echo If you have Chocolatey package manager:
echo    choco install maven
echo.
echo ============================================
echo After Installation:
echo ============================================
echo.
echo 1. Close this CMD and open a NEW one
echo 2. Verify: mvn -version
echo 3. Run backend: mvn spring-boot:run
echo.
echo ============================================
echo.

set /p OPEN_BROWSER="Open Maven download page in browser? (Y/N): "
if /i "%OPEN_BROWSER%"=="Y" (
    start https://maven.apache.org/download.cgi
    echo Browser opened!
)

echo.
set /p OPEN_INSTRUCTIONS="Open PATH Environment Variables settings? (Y/N): "
if /i "%OPEN_INSTRUCTIONS%"=="Y" (
    start sysdm.cpl
    echo.
    echo Steps:
    echo 1. Click "Advanced" tab
    echo 2. Click "Environment Variables"
    echo 3. Select "Path" under System Variables
    echo 4. Click "Edit"
    echo 5. Click "New"
    echo 6. Add: C:\maven\apache-maven-3.9.9\bin
    echo 7. Click OK on all dialogs
)

echo.
echo See MAVEN_MANUAL_INSTALL.txt for complete instructions!
echo.
pause
