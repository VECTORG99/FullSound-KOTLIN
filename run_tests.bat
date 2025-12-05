@echo off
REM Script para ejecutar tests de FullSound

echo ========================================
echo  Tests de FullSound - Android App
echo ========================================
echo.

:menu
echo Seleccione una opcion:
echo.
echo 1. Ejecutar TODOS los tests unitarios
echo 2. Ejecutar tests de ViewModels
echo 3. Ejecutar tests de Modelos
echo 4. Ejecutar tests de Utils
echo 5. Ejecutar tests de API (Fixer)
echo 6. Ejecutar tests instrumentados (requiere dispositivo/emulador)
echo 7. Generar reporte de tests
echo 8. Limpiar y ejecutar todos los tests
echo 9. Salir
echo.

set /p opcion="Ingrese el numero de la opcion: "

if "%opcion%"=="1" goto test_all
if "%opcion%"=="2" goto test_viewmodels
if "%opcion%"=="3" goto test_models
if "%opcion%"=="4" goto test_utils
if "%opcion%"=="5" goto test_api
if "%opcion%"=="6" goto test_instrumented
if "%opcion%"=="7" goto test_report
if "%opcion%"=="8" goto test_clean
if "%opcion%"=="9" goto end

echo Opcion invalida
pause
cls
goto menu

:test_all
echo.
echo Ejecutando TODOS los tests unitarios...
echo.
call gradlew.bat test
goto after_test

:test_viewmodels
echo.
echo Ejecutando tests de ViewModels...
echo.
call gradlew.bat test --tests "com.grupo8.fullsound.viewmodel.*"
goto after_test

:test_models
echo.
echo Ejecutando tests de Modelos...
echo.
call gradlew.bat test --tests "com.grupo8.fullsound.model.*"
goto after_test

:test_utils
echo.
echo Ejecutando tests de Utils...
echo.
call gradlew.bat test --tests "com.grupo8.fullsound.utils.*"
goto after_test

:test_api
echo.
echo Ejecutando tests de API (Fixer)...
echo.
call gradlew.bat test --tests "com.grupo8.fullsound.data.remote.fixer.*"
goto after_test

:test_instrumented
echo.
echo Ejecutando tests instrumentados...
echo NOTA: Asegurese de tener un dispositivo o emulador conectado
echo.
pause
call gradlew.bat connectedAndroidTest
goto after_test

:test_report
echo.
echo Ejecutando tests y generando reporte...
echo.
call gradlew.bat test
echo.
echo Reporte generado en: app\build\reports\tests\testDebugUnitTest\index.html
echo.
set /p abrir="Desea abrir el reporte? (s/n): "
if /i "%abrir%"=="s" start app\build\reports\tests\testDebugUnitTest\index.html
goto after_test

:test_clean
echo.
echo Limpiando proyecto y ejecutando todos los tests...
echo.
call gradlew.bat clean test
goto after_test

:after_test
echo.
echo ========================================
echo Tests completados
echo ========================================
echo.
set /p continuar="Desea ejecutar mas tests? (s/n): "
if /i "%continuar%"=="s" (
    cls
    goto menu
)

:end
echo.
echo Gracias por usar el script de tests!
pause

