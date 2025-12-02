package com.grupo8.fullsound.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo Beat adaptado a la estructura de la tabla 'beat' de Supabase
 */
@Entity(tableName = "beats")
data class Beat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val slug: String? = null,
    val artista: String? = null,
    val precio: Double = 0.0, // Precio en la moneda configurada (CLP por defecto en Supabase)
    val bpm: Int? = null,
    val tonalidad: String? = null,
    val duracion: Int? = null, // Duración en segundos
    val genero: String? = null,
    val etiquetas: String? = null, // Tags separados por comas
    val descripcion: String? = null,
    val imagenPath: String? = null, // imagen_url en Supabase
    val mp3Path: String? = null, // audio_url en Supabase
    val audioDemoPath: String? = null, // audio_demo_url en Supabase
    val reproducciones: Int = 0,
    val estado: String = "DISPONIBLE",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

