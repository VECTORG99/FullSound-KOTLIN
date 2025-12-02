@echo off
REM Script rapido para ejecutar tests especificos

if "%1"=="" (
    echo Uso: quick_test.bat [all^|models^|viewmodels^|utils^|api]
    echo.
    echo Ejemplos:
    echo   quick_test.bat all        - Ejecuta todos los tests
    echo   quick_test.bat models     - Ejecuta tests de modelos
    echo   quick_test.bat viewmodels - Ejecuta tests de ViewModels
    echo   quick_test.bat utils      - Ejecuta tests de utils
    echo   quick_test.bat api        - Ejecuta tests de API
    exit /b 1
)

if "%1"=="all" (
    echo Ejecutando todos los tests...
    call gradlew.bat test
    goto end
)

if "%1"=="models" (
    echo Ejecutando tests de modelos...
    call gradlew.bat test --tests "com.grupo8.fullsound.model.*"
    goto end
)

if "%1"=="viewmodels" (
    echo Ejecutando tests de ViewModels...
    call gradlew.bat test --tests "com.grupo8.fullsound.viewmodel.*"
    goto end
)

if "%1"=="utils" (
    echo Ejecutando tests de Utils...
    call gradlew.bat test --tests "com.grupo8.fullsound.utils.*"
    goto end
)

if "%1"=="api" (
    echo Ejecutando tests de API...
    call gradlew.bat test --tests "com.grupo8.fullsound.data.remote.fixer.*"
    goto end
)

echo Opcion invalida: %1

:end

