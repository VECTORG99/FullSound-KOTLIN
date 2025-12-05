package com.grupo8.fullsound.data.remote.supabase

import com.grupo8.fullsound.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Cliente singleton de Supabase para la aplicación.
 * Utiliza las credenciales del archivo .env para conectarse a la base de datos.
 */
object SupabaseClient {

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
    }

    /**
     * Función de prueba para verificar la configuración
     */
    fun getConnectionInfo(): String {
        return """
            Supabase URL: ${BuildConfig.SUPABASE_URL}
            Anon Key (primeros 20 chars): ${BuildConfig.SUPABASE_ANON_KEY.take(20)}...
            Cliente inicializado: ${try { client; "✅ Sí" } catch (e: Exception) { "❌ No: ${e.message}" }}
        """.trimIndent()
    }
}

