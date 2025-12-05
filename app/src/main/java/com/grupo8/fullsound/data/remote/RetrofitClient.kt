package com.grupo8.fullsound.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.grupo8.fullsound.BuildConfig
import com.grupo8.fullsound.data.remote.api.*
import com.grupo8.fullsound.data.remote.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit configurado para conectarse al Backend Spring Boot
 * URL base leída desde BuildConfig (local.properties)
 */
object RetrofitClient {
    
    // URL base del backend (se lee desde local.properties)
    private const val BASE_URL = BuildConfig.BACKEND_BASE_URL
    
    // Timeout en segundos
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    /**
     * Configurar Gson para manejar fechas y nulls
     */
    private val gson = GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()
    
    /**
     * Inicializar el cliente Retrofit con contexto para el interceptor
     */
    fun initialize(context: Context) {
        // Guardar contexto de aplicación
        appContext = context.applicationContext
    }
    
    private var appContext: Context? = null
    
    /**
     * Cliente OkHttp con interceptores
     */
    private fun getOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Instancia de Retrofit
     */
    private fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    // === API Services ===
    
    /**
     * API Service para Autenticación
     */
    fun getAuthApiService(context: Context): AuthApiService {
        return getRetrofit(context).create(AuthApiService::class.java)
    }
    
    /**
     * API Service para Beats
     */
    fun getBeatApiService(context: Context): BeatApiService {
        return getRetrofit(context).create(BeatApiService::class.java)
    }
    
    /**
     * API Service para Pedidos
     */
    fun getPedidoApiService(context: Context): PedidoApiService {
        return getRetrofit(context).create(PedidoApiService::class.java)
    }
    
    /**
     * API Service para Usuarios
     */
    fun getUsuarioApiService(context: Context): UsuarioApiService {
        return getRetrofit(context).create(UsuarioApiService::class.java)
    }
    
    /**
     * API Service para Pagos
     */
    fun getPagoApiService(context: Context): PagoApiService {
        return getRetrofit(context).create(PagoApiService::class.java)
    }
    
    // Mantener compatibilidad con código antiguo (deprecated)
    @Deprecated("Use getAuthApiService, getBeatApiService, etc.", ReplaceWith("getBeatApiService(context)"))
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

