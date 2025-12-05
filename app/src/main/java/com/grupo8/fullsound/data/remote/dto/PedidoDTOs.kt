package com.grupo8.fullsound.data.remote.dto

/**
 * DTOs para Pedidos - Compatible con Backend Spring Boot
 */

/**
 * Request para crear un nuevo pedido
 * Endpoint: POST /api/pedidos
 */
data class PedidoRequestDto(
    val items: List<PedidoItemRequestDto>,
    val total: Int,             // Total en CLP (pesos chilenos)
    val metodoPago: String      // STRIPE, MERCADOPAGO, WEBPAY, etc.
)

/**
 * Item individual para el request de pedido
 */
data class PedidoItemRequestDto(
    val idBeat: Int,
    val cantidad: Int,
    val precioUnitario: Int     // Precio unitario en CLP
)

/**
 * Response completo de un pedido
 */
data class PedidoResponseDto(
    val id: Int,
    val numeroPedido: String,
    val fechaCompra: String,    // ISO 8601 format
    val total: Int,
    val estado: String,         // PENDIENTE, CONFIRMADO, CANCELADO, COMPLETADO
    val metodoPago: String?,
    val items: List<PedidoItemResponseDto>,
    val usuario: UsuarioDto? = null
)

/**
 * Item individual en la response del pedido
 */
data class PedidoItemResponseDto(
    val id: Int,
    val beat: BeatResponseDto,
    val cantidad: Int,
    val precioUnitario: Int,
    val subtotal: Int
)

/**
 * Request para actualizar estado del pedido
 * Endpoint: PATCH /api/pedidos/{id}/estado?estado={nuevo_estado}
 */
data class ActualizarEstadoPedidoDto(
    val estado: String  // PENDIENTE, CONFIRMADO, CANCELADO, COMPLETADO
)
