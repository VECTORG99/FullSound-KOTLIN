package com.grupo8.fullsound.data.remote.api

import com.grupo8.fullsound.data.remote.dto.*
import retrofit2.http.*

/**
 * API Service para Usuarios
 * Base URL: /api/usuarios
 */
interface UsuarioApiService {
    
    /**
     * Obtener información del usuario autenticado
     * GET /api/usuarios/me
     */
    @GET("usuarios/me")
    suspend fun getCurrentUser(): UsuarioDto
    
    /**
     * Obtener un usuario por ID
     * GET /api/usuarios/{id}
     */
    @GET("usuarios/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): UsuarioDto
    
    /**
     * Obtener todos los usuarios (requiere rol ADMIN)
     * GET /api/usuarios
     */
    @GET("usuarios")
    suspend fun getAllUsers(): List<UsuarioDto>
    
    /**
     * Cambiar contraseña del usuario autenticado
     * POST /api/usuarios/cambiar-password
     */
    @POST("usuarios/cambiar-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): MessageResponseDto
}
