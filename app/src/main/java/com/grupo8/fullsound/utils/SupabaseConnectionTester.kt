package com.grupo8.fullsound.utils

import android.content.Context
import android.util.Log
import com.grupo8.fullsound.BuildConfig
import com.grupo8.fullsound.data.remote.supabase.repository.SupabaseBeatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Utilidad para probar la conexión a Supabase
 */
object SupabaseConnectionTester {

    private const val TAG = "SupabaseTest"

    /**
     * Prueba la configuración de Supabase
     */
    fun testConfiguration(context: Context) {
        Log.d(TAG, "=== Iniciando prueba de Supabase ===")

        // 1. Verificar variables de entorno
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_ANON_KEY

        Log.d(TAG, "SUPABASE_URL: $supabaseUrl")
        Log.d(TAG, "SUPABASE_ANON_KEY existe: ${supabaseKey.isNotEmpty()}")
        Log.d(TAG, "SUPABASE_ANON_KEY length: ${supabaseKey.length}")

        if (supabaseUrl.isEmpty()) {
            Log.e(TAG, "❌ ERROR: SUPABASE_URL está vacía")
            return
        }

        if (supabaseKey.isEmpty()) {
            Log.e(TAG, "❌ ERROR: SUPABASE_ANON_KEY está vacía")
            return
        }

        Log.d(TAG, "✅ Variables de entorno configuradas correctamente")
    }

    /**
     * Prueba la conexión a Supabase obteniendo beats
     */
    fun testConnection(context: Context, onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d(TAG, "=== Probando conexión a Supabase ===")

                val repository = SupabaseBeatRepository()

                withContext(Dispatchers.IO) {
                    val beats = repository.getAllBeats()

                    withContext(Dispatchers.Main) {
                        if (beats.isEmpty()) {
                            Log.w(TAG, "⚠️ Conexión exitosa pero no hay beats en la base de datos")
                            onResult(true, "Conexión exitosa. La tabla 'beats' está vacía. Necesitas insertar datos.")
                        } else {
                            Log.d(TAG, "✅ Conexión exitosa. Se encontraron ${beats.size} beats")
                            beats.forEachIndexed { index, beat ->
                                Log.d(TAG, "Beat ${index + 1}: ${beat.titulo} - ${beat.artista}")
                            }
                            onResult(true, "Conexión exitosa. Se encontraron ${beats.size} beats en Supabase.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al conectar con Supabase", e)
                Log.e(TAG, "Tipo de error: ${e.javaClass.simpleName}")
                Log.e(TAG, "Mensaje: ${e.message}")
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    onResult(false, "Error de conexión: ${e.message}")
                }
            }
        }
    }

    /**
     * Prueba completa: configuración + conexión
     */
    fun runFullTest(context: Context, onResult: (Boolean, String) -> Unit) {
        // Primero probar configuración
        testConfiguration(context)

        // Luego probar conexión
        testConnection(context, onResult)
    }
}

