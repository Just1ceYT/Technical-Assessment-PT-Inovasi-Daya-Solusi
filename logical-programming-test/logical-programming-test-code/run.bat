@echo off
echo ================================
echo IT Logical Programming Test
echo ================================

echo Compiling Java program...
javac Main.java

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo.
    echo Running program...
    echo ================================
    java Main
    echo ================================
) else (
    echo.
    echo Compilation failed! Please check if Java is installed.
    echo Make sure you have JDK installed and added to PATH.
)

echo.
pause