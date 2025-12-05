package com.grupo8.fullsound.data.remote.dto

/**
 * DTO para respuestas de error del backend Spring Boot
 * Estructura estándar de Spring Boot para errores HTTP
 */
data class ErrorResponseDto(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)

/**
 * Wrapper para manejar respuestas exitosas y errores de forma unificada
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
