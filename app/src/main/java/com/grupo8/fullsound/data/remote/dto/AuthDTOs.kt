package com.grupo8.fullsound.data.remote.dto

/**
 * DTOs para Autenticación - Compatible con Backend Spring Boot
 */

/**
 * Request para login
 * Endpoint: POST /api/auth/login
 */
data class LoginRequestDto(
    val correo: String,      // Backend usa "correo" no "email"
    val contraseña: String   // Backend usa "contraseña" con ñ
)

/**
 * Request para registro de usuario
 * Endpoint: POST /api/auth/register
 */
data class RegisterRequestDto(
    val nombreUsuario: String,
    val correo: String,
    val contraseña: String,
    val nombre: String? = null,
    val apellido: String? = null
)

/**
 * Response de autenticación (login/register)
 */
data class AuthResponseDto(
    val token: String,
    val usuario: UsuarioDto
)

/**
 * DTO de Usuario - Compatible con entidad Usuario.java del backend
 */
data class UsuarioDto(
    val id: Int,
    val nombreUsuario: String,
    val correo: String,
    val activo: Boolean,
    val rol: RolDto,
    val nombre: String? = null,
    val apellido: String? = null
)

/**
 * DTO de Rol
 */
data class RolDto(
    val id: Int,
    val nombre: String  // ADMIN, USER, etc.
)

/**
 * Request para cambio de contraseña
 * Endpoint: POST /api/usuarios/cambiar-password
 */
data class ChangePasswordRequestDto(
    val contraseñaActual: String,
    val contraseñaNueva: String
)

/**
 * Response genérico con mensaje
 */
data class MessageResponseDto(
    val message: String
)
