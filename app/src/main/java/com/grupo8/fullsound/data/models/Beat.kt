package com.grupo8.fullsound.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beats")
data class Beat(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val bpm: Int,
    val genre: String,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val isActive: Boolean = true
)

data class BeatResponse(
    val beats: List<Beat>
)