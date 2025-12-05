package com.grupo8.fullsound.data.remote.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor para agregar el token JWT a todas las peticiones HTTP
 * Excluye endpoints de autenticación que no requieren token
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    
    companion object {
        private const val AUTH_PREFS = "auth_prefs"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Obtener token JWT almacenado en SharedPreferences
        val sharedPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        val token = sharedPrefs.getString(KEY_JWT_TOKEN, null)
        
        // Verificar si el endpoint actual es un endpoint de autenticación
        val isAuthEndpoint = originalRequest.url.encodedPath.let { path ->
            path.contains("/auth/login") || 
            path.contains("/auth/register") ||
            path.contains("/auth/health")
        }
        
        // Si hay token y no es un endpoint de auth, agregar el header Authorization
        val newRequest = if (token != null && !isAuthEndpoint) {
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, "$TOKEN_PREFIX$token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
