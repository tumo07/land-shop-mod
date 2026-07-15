@echo off
setlocal enabledelayedexpansion

echo =============================================
echo   LandShop Mod - Direct Build (No Gradle)
echo =============================================
echo.

:: ==========================================
:: CONFIGURATION
:: Modify these paths if the script cannot find them automatically.
:: ==========================================
set "JAVAC_PATH=C:\Program Files\Java\jdk-26.0.1\bin\javac.exe"
set "JAR_TOOL_PATH=C:\Program Files\Java\jdk-26.0.1\bin\jar.exe"
set "SERVER_DIR=C:\Your\Fabric\Server\Path"
:: ==========================================

if not exist "%JAVAC_PATH%" (
    echo [Setup] Javac not found at %JAVAC_PATH%.
    set /p JAVAC_PATH="Please enter the full path to javac.exe: "
)
if not exist "%JAR_TOOL_PATH%" (
    echo [Setup] Jar tool not found at %JAR_TOOL_PATH%.
    set /p JAR_TOOL_PATH="Please enter the full path to jar.exe: "
)
if not exist "%SERVER_DIR%\versions" (
    echo [Setup] Fabric server not found at %SERVER_DIR%.
    set /p SERVER_DIR="Please enter the path to your Fabric Server directory: "
)

set "MOD_DIR=%cd%"
set "BUILD_DIR=%MOD_DIR%\build"
set "CLASSES_DIR=%BUILD_DIR%\classes"
set "OUTPUT_DIR=%BUILD_DIR%\libs"

REM Clean previous build
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%CLASSES_DIR%"
mkdir "%OUTPUT_DIR%"

REM Build classpath from server JARs
set "CP="
for /R "%SERVER_DIR%\versions" %%f in (server-*.jar) do set "CP=!CP!;%%f"
for /R "%SERVER_DIR%\libraries" %%f in (*.jar) do set "CP=!CP!;%%f"
for /R "%SERVER_DIR%\.fabric\processedMods" %%f in (*.jar) do set "CP=!CP!;%%f"

echo Classpath configured.
echo.

REM Compile
echo [1/3] Compiling LandShopMod.java...
if not exist "build\classes\java\main" mkdir "build\classes\java\main"
REM Find all java files
dir /s /b src\main\java\*.java > sources.txt
"%JAVAC_PATH%" -d "build\classes\java\main" -cp "%CP%" @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo COMPILATION FAILED
    pause
    exit /b %errorlevel%
)

echo [2/3] Packaging JAR...
if not exist "build\libs" mkdir "build\libs"
"%JAR_TOOL_PATH%" cf "build\libs\land-shop-1.0.0.jar" -C "build\classes\java\main" . -C "src\main\resources" .

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo PACKAGING FAILED
    pause
    exit /b %errorlevel%
)

echo [3/3] Installing to mods folder...
copy /Y "build\libs\land-shop-1.0.0.jar" "%SERVER_DIR%\mods\"

echo.
echo =============================================
echo   BUILD SUCCESSFUL
echo   Output: %CD%\build\libs\land-shop-1.0.0.jar
echo   Installed to: %SERVER_DIR%\mods\
echo   Restart your server to load the mod.
echo =============================================
echo.
pause
