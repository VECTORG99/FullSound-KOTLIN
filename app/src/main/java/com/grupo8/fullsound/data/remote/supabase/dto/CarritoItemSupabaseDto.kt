package com.grupo8.fullsound.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object para la tabla 'carrito_items' en Supabase
 */
@Serializable
data class CarritoItemSupabaseDto(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("user_id")
    val userId: String,

    @SerialName("beat_id")
    val beatId: Int,

    @SerialName("cantidad")
    val cantidad: Int,

    @SerialName("precio_unitario")
    val precioUnitario: Double,

    @SerialName("created_at")
    val createdAt: String? = null
)

