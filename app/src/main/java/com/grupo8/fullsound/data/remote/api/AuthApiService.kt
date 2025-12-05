package com.grupo8.fullsound.data.remote.api

import com.grupo8.fullsound.data.remote.dto.*
import retrofit2.http.*

/**
 * API Service para Autenticación
 * Base URL: /api/auth
 */
interface AuthApiService {
    
    /**
     * Registrar un nuevo usuario
     * POST /api/auth/register
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): MessageResponseDto
    
    /**
     * Login de usuario
     * POST /api/auth/login
     * @return Token JWT y datos del usuario
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): AuthResponseDto
    
    /**
     * Health check del servidor
     * GET /api/auth/health
     */
    @GET("auth/health")
    suspend fun healthCheck(): MessageResponseDto
}
