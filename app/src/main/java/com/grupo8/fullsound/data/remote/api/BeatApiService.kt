package com.grupo8.fullsound.data.remote.api

import com.grupo8.fullsound.data.remote.dto.*
import retrofit2.http.*

/**
 * API Service para Beats
 * Base URL: /api/beats
 */
interface BeatApiService {
    
    /**
     * Obtener todos los beats activos
     * GET /api/beats
     */
    @GET("beats")
    suspend fun getAllBeats(): List<BeatResponseDto>
    
    /**
     * Obtener un beat por ID
     * GET /api/beats/{id}
     */
    @GET("beats/{id}")
    suspend fun getBeatById(
        @Path("id") id: Int
    ): BeatResponseDto
    
    /**
     * Obtener un beat por slug
     * GET /api/beats/slug/{slug}
     */
    @GET("beats/slug/{slug}")
    suspend fun getBeatBySlug(
        @Path("slug") slug: String
    ): BeatResponseDto
    
    /**
     * Buscar beats por término
     * GET /api/beats/search?q={query}
     */
    @GET("beats/search")
    suspend fun searchBeats(
        @Query("q") query: String
    ): List<BeatResponseDto>
    
    /**
     * Obtener beats destacados
     * GET /api/beats/featured?limit={n}
     */
    @GET("beats/featured")
    suspend fun getFeaturedBeats(
        @Query("limit") limit: Int = 10
    ): List<BeatResponseDto>
    
    /**
     * Crear un nuevo beat (requiere rol ADMIN)
     * POST /api/beats
     */
    @POST("beats")
    suspend fun createBeat(
        @Body request: BeatRequestDto
    ): BeatResponseDto
    
    /**
     * Actualizar un beat existente (requiere rol ADMIN)
     * PUT /api/beats/{id}
     */
    @PUT("beats/{id}")
    suspend fun updateBeat(
        @Path("id") id: Int,
        @Body request: BeatRequestDto
    ): BeatResponseDto
    
    /**
     * Eliminar un beat (requiere rol ADMIN)
     * DELETE /api/beats/{id}
     */
    @DELETE("beats/{id}")
    suspend fun deleteBeat(
        @Path("id") id: Int
    ): MessageResponseDto
}
