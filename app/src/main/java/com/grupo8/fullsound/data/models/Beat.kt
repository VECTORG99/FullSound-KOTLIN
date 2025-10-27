package com.grupo8.fullsound.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beats")
data class Beat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val artista: String,
    val bpm: Int,
    val imagenPath: String, // Ruta local o URL de la imagen
    val mp3Path: String    // Ruta local o URL del archivo mp3
)

