package com.grupo8.fullsound.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de item del carrito de compras
 * Adaptado para compatibilidad con Beat que tiene campos opcionales
 */
@Entity(tableName = "carrito")
data class CarritoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val beatId: Int,
    val titulo: String,
    val artista: String? = null,
    val precio: Double,
    val imagenPath: String? = null,
    val cantidad: Int = 1
)

