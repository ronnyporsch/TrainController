@echo off
echo === Building frontend ===
call frontend\gradlew -p frontend composeApp:createDistributable
if errorlevel 1 (
    echo Frontend build failed.
    exit /b %errorlevel%
)

echo === Building backend ===
dotnet publish backend -c Release
if errorlevel 1 (
    echo Backend build failed.
    exit /b %errorlevel%
)

echo Copying final distributables to dist dir...
robocopy frontend\composeApp\build\compose\binaries\main\app\TrainController dist /E /NFL /NDL /NJH /NJS
robocopy backend\backendBinaries dist/backendBinaries /E /NFL /NDL /NJH /NJS

echo === All done ===