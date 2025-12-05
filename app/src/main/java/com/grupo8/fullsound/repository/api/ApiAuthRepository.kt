package com.grupo8.fullsound.repository.api

import android.content.Context
import android.util.Log
import com.grupo8.fullsound.data.remote.RetrofitClient
import com.grupo8.fullsound.data.remote.dto.*
import com.grupo8.fullsound.data.remote.interceptor.TokenManager
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones de autenticación con el backend Spring Boot
 */
class ApiAuthRepository(private val context: Context) {
    
    private val TAG = "ApiAuthRepository"
    private val authApiService = RetrofitClient.getAuthApiService(context)
    private val tokenManager = TokenManager(context)
    
    /**
     * Login de usuario
     * @return Resource con AuthResponseDto si es exitoso
     */
    suspend fun login(email: String, password: String): Resource<AuthResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Intentando login para: $email")
                
                val request = LoginRequestDto(
                    correo = email,
                    contraseña = password
                )
                
                val response = authApiService.login(request)
                
                // Guardar token y datos del usuario
                tokenManager.saveToken(
                    token = response.token,
                    userId = response.usuario.id,
                    username = response.usuario.nombreUsuario,
                    email = response.usuario.correo,
                    role = response.usuario.rol.nombre
                )
                
                Log.d(TAG, "✅ Login exitoso para: ${response.usuario.nombreUsuario}")
                Resource.Success(response)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en login", e)
                Resource.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }
    
    /**
     * Registro de nuevo usuario
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        nombre: String? = null,
        apellido: String? = null
    ): Resource<MessageResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Intentando registro para: $email")
                
                val request = RegisterRequestDto(
                    nombreUsuario = username,
                    correo = email,
                    contraseña = password,
                    nombre = nombre,
                    apellido = apellido
                )
                
                val response = authApiService.register(request)
                
                Log.d(TAG, "✅ Registro exitoso: ${response.message}")
                Resource.Success(response)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en registro", e)
                Resource.Error("Error al registrar usuario: ${e.message}")
            }
        }
    }
    
    /**
     * Verificar si hay un usuario autenticado
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
    
    /**
     * Obtener token actual
     */
    fun getToken(): String? {
        return tokenManager.getToken()
    }
    
    /**
     * Obtener datos del usuario actual desde el token
     */
    fun getCurrentUserData(): Map<String, Any?> {
        return mapOf(
            "userId" to tokenManager.getUserId(),
            "username" to tokenManager.getUsername(),
            "email" to tokenManager.getUserEmail(),
            "role" to tokenManager.getUserRole(),
            "isAdmin" to tokenManager.isAdmin()
        )
    }
    
    /**
     * Cerrar sesión
     */
    fun logout() {
        Log.d(TAG, "Cerrando sesión...")
        tokenManager.clearToken()
    }
    
    /**
     * Health check del backend
     */
    suspend fun healthCheck(): Resource<MessageResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.healthCheck()
                Log.d(TAG, "✅ Backend health: ${response.message}")
                Resource.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Backend health check failed", e)
                Resource.Error("Backend no disponible: ${e.message}")
            }
        }
    }
}
