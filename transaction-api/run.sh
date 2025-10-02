@echo off
echo ===============================================
echo Transaction API Server - Setup and Run
echo ===============================================

echo Step 1: Creating output directory...
if not exist "out" mkdir out

echo Step 2: Compiling Java files...
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d out src/*.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo Step 3: Starting Transaction API Server...
java -cp "out;lib/mysql-connector-j-8.0.33.jar" Main

pause