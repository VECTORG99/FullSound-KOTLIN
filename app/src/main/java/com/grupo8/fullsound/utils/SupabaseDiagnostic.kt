package com.grupo8.fullsound.utils

import android.content.Context
import android.util.Log
import com.grupo8.fullsound.data.remote.supabase.SupabaseClient
import com.grupo8.fullsound.data.remote.supabase.repository.SupabaseBeatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Utilidad para diagnosticar problemas de conexión con Supabase
 */
object SupabaseDiagnostic {

    private const val TAG = "SupabaseDiagnostic"

    /**
     * Ejecuta una serie de pruebas de diagnóstico de Supabase
     */
    fun runDiagnostics(context: Context, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val results = StringBuilder()
            results.append("🔍 DIAGNÓSTICO DE SUPABASE\n")
            results.append("=" .repeat(50)).append("\n\n")

            // Test 1: Verificar configuración
            results.append("1️⃣ VERIFICANDO CONFIGURACIÓN...\n")
            try {
                val info = SupabaseClient.getConnectionInfo()
                results.append("✅ Configuración cargada:\n")
                results.append(info).append("\n\n")
                Log.d(TAG, "✅ Configuración OK")
            } catch (e: Exception) {
                results.append("❌ Error en configuración: ${e.message}\n\n")
                Log.e(TAG, "❌ Error en configuración", e)
            }

            // Test 2: Probar inicialización del cliente
            results.append("2️⃣ PROBANDO CLIENTE SUPABASE...\n")
            try {
                val client = SupabaseClient.client
                results.append("✅ Cliente Supabase inicializado correctamente\n\n")
                Log.d(TAG, "✅ Cliente inicializado")
            } catch (e: Exception) {
                results.append("❌ Error al inicializar cliente: ${e.message}\n\n")
                Log.e(TAG, "❌ Error al inicializar cliente", e)
                callback(results.toString())
                return@launch
            }

            // Test 3: Probar conexión a tabla beat
            results.append("3️⃣ PROBANDO CONEXIÓN A TABLA 'beat'...\n")
            try {
                val beats = withContext(Dispatchers.IO) {
                    SupabaseBeatRepository().getAllBeats()
                }

                results.append("✅ Conexión exitosa a tabla 'beat'\n")
                results.append("📊 Beats encontrados: ${beats.size}\n\n")

                if (beats.isEmpty()) {
                    results.append("⚠️ ADVERTENCIA: La tabla está vacía\n")
                    results.append("   Asegúrate de que hay datos en la tabla 'beat' de Supabase\n\n")
                } else {
                    results.append("📋 PRIMEROS 3 BEATS:\n")
                    beats.take(3).forEachIndexed { index, beat ->
                        results.append("   ${index + 1}. ${beat.titulo}\n")
                        results.append("      Artista: ${beat.artista ?: "N/A"}\n")
                        results.append("      Precio: ${beat.precio} CLP\n")
                        results.append("      ID: ${beat.id}\n\n")
                    }
                }

                Log.d(TAG, "✅ Conexión a tabla beat OK - ${beats.size} beats")
            } catch (e: Exception) {
                results.append("❌ Error al conectar con tabla 'beat':\n")
                results.append("   ${e.javaClass.simpleName}: ${e.message}\n\n")
                Log.e(TAG, "❌ Error en tabla beat", e)

                // Información adicional de debug
                results.append("🔍 Stack trace:\n")
                results.append(e.stackTraceToString().take(500)).append("...\n\n")
            }

            // Test 4: Verificar permisos de la tabla
            results.append("4️⃣ VERIFICANDO PERMISOS...\n")
            results.append("ℹ️ Si los tests anteriores fallaron, verifica:\n")
            results.append("   • RLS (Row Level Security) está configurado correctamente\n")
            results.append("   • La tabla 'beat' existe en Supabase\n")
            results.append("   • El usuario anon tiene permisos de SELECT\n")
            results.append("   • La URL y ANON_KEY son correctas\n\n")

            results.append("=" .repeat(50)).append("\n")
            results.append("✅ Diagnóstico completado\n")

            Log.d(TAG, "Diagnóstico completado")
            callback(results.toString())
        }
    }

    /**
     * Prueba rápida de conexión (solo verifica si puede obtener datos)
     */
    suspend fun quickConnectionTest(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val beats = SupabaseBeatRepository().getAllBeats()
                Log.d(TAG, "✅ Prueba rápida exitosa: ${beats.size} beats")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Prueba rápida falló", e)
            false
        }
    }
}

