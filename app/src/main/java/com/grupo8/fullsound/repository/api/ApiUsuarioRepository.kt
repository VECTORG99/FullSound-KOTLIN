package com.grupo8.fullsound.repository.api

import android.content.Context
import android.util.Log
import com.grupo8.fullsound.data.remote.RetrofitClient
import com.grupo8.fullsound.data.remote.dto.ChangePasswordRequestDto
import com.grupo8.fullsound.data.remote.dto.MessageResponseDto
import com.grupo8.fullsound.data.remote.dto.UsuarioDto
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones con Usuarios usando el backend Spring Boot
 */
class ApiUsuarioRepository(private val context: Context) {
    
    private val TAG = "ApiUsuarioRepository"
    private val usuarioApiService = RetrofitClient.getUsuarioApiService(context)
    
    /**
     * Obtener información del usuario autenticado
     */
    suspend fun getCurrentUser(): Resource<UsuarioDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo usuario actual...")
                val usuario = usuarioApiService.getCurrentUser()
                Log.d(TAG, "✅ Usuario actual: ${usuario.nombreUsuario}")
                Resource.Success(usuario)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener usuario actual", e)
                Resource.Error("Error al cargar usuario: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener un usuario por ID
     */
    suspend fun getUserById(id: Int): Resource<UsuarioDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo usuario ID: $id")
                val usuario = usuarioApiService.getUserById(id)
                Log.d(TAG, "✅ Usuario obtenido: ${usuario.nombreUsuario}")
                Resource.Success(usuario)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener usuario por ID", e)
                Resource.Error("Error al cargar usuario: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener todos los usuarios (requiere rol ADMIN)
     */
    suspend fun getAllUsers(): Resource<List<UsuarioDto>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo todos los usuarios (admin)...")
                val usuarios = usuarioApiService.getAllUsers()
                Log.d(TAG, "✅ ${usuarios.size} usuarios obtenidos")
                Resource.Success(usuarios)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener todos los usuarios", e)
                Resource.Error("Error al cargar usuarios: ${e.message}")
            }
        }
    }
    
    /**
     * Cambiar contraseña del usuario autenticado
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Resource<MessageResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Cambiando contraseña del usuario...")
                val request = ChangePasswordRequestDto(
                    contraseñaActual = currentPassword,
                    contraseñaNueva = newPassword
                )
                val response = usuarioApiService.changePassword(request)
                Log.d(TAG, "✅ ${response.message}")
                Resource.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cambiar contraseña", e)
                Resource.Error("Error al cambiar contraseña: ${e.message}")
            }
        }
    }
}
