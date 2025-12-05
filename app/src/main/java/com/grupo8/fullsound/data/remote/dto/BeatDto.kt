package com.grupo8.fullsound.data.remote.dto

// DTO mínimo para mapear la respuesta remota. Ajustar campos según la API real.
data class BeatDto(
    val id: Int,
    val title: String?,
    val artist: String?,
    val bpm: Int?,
    val image: String?,
    val mp3: String?,
    val price: Double?
)

