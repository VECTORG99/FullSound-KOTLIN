package com.grupo8.fullsound.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la tabla 'beat' de Supabase
 * Coincide exactamente con la estructura de la base de datos
 */
@Serializable
data class BeatSupabaseDto(
    @SerialName("id_beat")
    val idBeat: Int? = null,

    @SerialName("titulo")
    val titulo: String,

    @SerialName("slug")
    val slug: String? = null,

    @SerialName("artista")
    val artista: String? = null,

    @SerialName("precio")
    val precio: Int, // En Supabase es integer (precio en CLP)

    @SerialName("bpm")
    val bpm: Int? = null,

    @SerialName("tonalidad")
    val tonalidad: String? = null,

    @SerialName("duracion")
    val duracion: Int? = null,

    @SerialName("genero")
    val genero: String? = null,

    @SerialName("emocion")
    val emocion: String? = null,

    @SerialName("etiquetas")
    val etiquetas: String? = null,

    @SerialName("descripcion")
    val descripcion: String? = null,

    @SerialName("imagen_url")
    val imagenUrl: String? = null,

    @SerialName("audio_url")
    val audioUrl: String? = null,

    @SerialName("audio_demo_url")
    val audioDemoUrl: String? = null,

    @SerialName("reproducciones")
    val reproducciones: Int = 0,

    @SerialName("estado")
    val estado: String = "DISPONIBLE",

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

