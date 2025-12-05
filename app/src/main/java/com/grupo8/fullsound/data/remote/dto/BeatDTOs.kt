package com.grupo8.fullsound.data.remote.dto

/**
 * DTOs para Beats - Compatible con Backend Spring Boot
 */

/**
 * Response DTO de Beat - Compatible con entidad Beat.java del backend
 * Todos los campos coinciden con la base de datos PostgreSQL
 */
data class BeatResponseDto(
    val id: Int,                    // id_beat en la base de datos
    val titulo: String,
    val slug: String? = null,
    val artista: String? = null,
    val precio: Int,                // Precio en CLP (Integer en backend, no Double)
    val bpm: Int? = null,
    val tonalidad: String? = null,
    val duracion: Int? = null,      // Duración en segundos
    val genero: String? = null,
    val etiquetas: String? = null,  // Tags separados por comas
    val descripcion: String? = null,
    val imagenUrl: String? = null,
    val audioUrl: String? = null,
    val audioDemoUrl: String? = null,
    val reproducciones: Int = 0,
    val estado: String = "ACTIVO",  // ACTIVO, INACTIVO, PENDIENTE
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Request DTO para crear o actualizar un Beat
 * Endpoints: 
 *  - POST /api/beats (crear)
 *  - PUT /api/beats/{id} (actualizar)
 */
data class BeatRequestDto(
    val titulo: String,
    val artista: String? = null,
    val precio: Int,                // Precio en CLP (pesos chilenos)
    val bpm: Int? = null,
    val tonalidad: String? = null,
    val duracion: Int? = null,
    val genero: String? = null,
    val descripcion: String? = null,
    val imagenUrl: String? = null,
    val audioUrl: String? = null,
    val audioDemoUrl: String? = null
)
