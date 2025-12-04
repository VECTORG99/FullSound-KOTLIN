package com.grupo8.fullsound.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la tabla 'usuario' de Supabase
 * Coincide con la estructura de la base de datos
 */
@Serializable
data class UserSupabaseDto(
    @SerialName("id_usuario")
    val idUsuario: Int? = null,

    @SerialName("nombre_usuario")
    val nombreUsuario: String,

    @SerialName("correo")
    val correo: String,

    @SerialName("contraseña")
    val contrasena: String,

    @SerialName("activo")
    val activo: Boolean = true,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("id_rol")
    val idRol: Int? = null,

    @SerialName("apellido")
    val apellido: String? = null,

    @SerialName("nombre")
    val nombre: String? = null,

    @SerialName("rut")
    val rut: String? = null
)

