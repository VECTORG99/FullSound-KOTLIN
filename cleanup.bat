@echo off
echo ========================================
echo Limpiando archivos temporales...
echo ========================================
cd /d "%~dp0"
echo.
echo Eliminando .env...
if exist .env (del /f /q .env && echo [OK] .env eliminado) else (echo [--] .env no existe)
echo.
echo Eliminando CONFIGURACION_SIMPLE.md...
if exist CONFIGURACION_SIMPLE.md (del /f /q CONFIGURACION_SIMPLE.md && echo [OK] CONFIGURACION_SIMPLE.md eliminado) else (echo [--] CONFIGURACION_SIMPLE.md no existe)
echo.
echo Eliminando CONFIGURACION_SUPABASE.md...
if exist CONFIGURACION_SUPABASE.md (del /f /q CONFIGURACION_SUPABASE.md && echo [OK] CONFIGURACION_SUPABASE.md eliminado) else (echo [--] CONFIGURACION_SUPABASE.md no existe)
echo.
echo ========================================
echo Limpieza completada
echo ========================================
echo.
echo Este script se eliminara en 3 segundos...
timeout /t 3 >nul
del "%~f0"

