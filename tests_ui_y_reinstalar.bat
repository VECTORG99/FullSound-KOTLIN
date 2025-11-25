@echo off
REM Script para ejecutar tests instrumentados y reinstalar la app

echo ========================================
echo  Ejecutando Tests Instrumentados
echo ========================================
echo.

call gradlew.bat :app:connectedDebugAndroidTest

echo.
echo ========================================
echo  Tests completados
echo ========================================
echo.
echo Reinstalando la app...
echo.

call gradlew.bat :app:installDebug

echo.
echo ========================================
echo  App reinstalada correctamente
echo ========================================
echo.
pause

