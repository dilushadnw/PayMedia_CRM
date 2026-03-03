@echo off
echo ============================================
echo PayMedia CRM - Database Setup Script
echo ============================================
echo.

REM Check if MySQL is installed
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: MySQL not found in PATH!
    echo Please install MySQL or add it to your system PATH.
    echo.
    echo Common MySQL installation paths:
    echo - C:\Program Files\MySQL\MySQL Server 8.0\bin
    echo - C:\xampp\mysql\bin
    echo.
    pause
    exit /b 1
)

echo Found MySQL installation...
echo.

REM Prompt for MySQL root password
set /p MYSQL_PASSWORD="Enter MySQL root password (press Enter if no password): "

echo.
echo Creating database and tables...
echo.

REM Run MySQL commands
if "%MYSQL_PASSWORD%"=="" (
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS paymedia_crm;" 2>nul
    if %errorlevel% neq 0 (
        echo ERROR: Failed to create database. Please check your MySQL credentials.
        pause
        exit /b 1
    )
    mysql -u root paymedia_crm < organisation_schema.sql
) else (
    mysql -u root -p%MYSQL_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS paymedia_crm;" 2>nul
    if %errorlevel% neq 0 (
        echo ERROR: Failed to create database. Please check your MySQL credentials.
        pause
        exit /b 1
    )
    mysql -u root -p%MYSQL_PASSWORD% paymedia_crm < organisation_schema.sql
)

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo SUCCESS! Database setup completed!
    echo ============================================
    echo.
    echo Database: paymedia_crm
    echo Table: organisation
    echo Sample data: 3 organisations added
    echo.
) else (
    echo.
    echo ============================================
    echo ERROR! Database setup failed!
    echo ============================================
    echo.
    echo Please check:
    echo 1. MySQL server is running
    echo 2. MySQL credentials are correct
    echo 3. You have necessary permissions
    echo.
)

pause
