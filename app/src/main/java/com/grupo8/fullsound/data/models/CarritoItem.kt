package com.grupo8.fullsound.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class CarritoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val beatId: Int,
    val titulo: String,
    val artista: String,
    val precio: Double,
    val imagenPath: String,
    val cantidad: Int = 1
)

