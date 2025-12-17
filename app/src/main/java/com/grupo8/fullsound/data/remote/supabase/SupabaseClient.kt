package com.grupo8.fullsound.data.remote.supabase

import com.grupo8.fullsound.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Cliente singleton de Supabase para la aplicación.
 * Utiliza las credenciales del archivo .env para conectarse a la base de datos.
 */
@OptIn(ExperimentalSerializationApi::class)
object SupabaseClient {

    // Configuración de JSON con manejo robusto de errores
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        prettyPrint = false
        encodeDefaults = true
        explicitNulls = false
    }

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Realtime)
            install(Storage)

            // Configurar el serializador JSON
            defaultSerializer = KotlinXSerializer(jsonConfig)
        }
    }

    /**
     * Función de prueba para verificar la configuración
     */
    fun getConnectionInfo(): String {
        return """
            Supabase URL: ${BuildConfig.SUPABASE_URL}
            Anon Key (primeros 20 chars): ${BuildConfig.SUPABASE_ANON_KEY.take(20)}...
            Cliente inicializado: ${try { client; "Si" } catch (e: Exception) { "No: ${e.message}" }}
        """.trimIndent()
    }
}

