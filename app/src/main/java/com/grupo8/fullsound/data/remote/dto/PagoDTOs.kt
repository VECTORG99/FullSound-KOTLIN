package com.grupo8.fullsound.data.remote.dto

/**
 * DTOs para Pagos (Stripe Integration) - Compatible con Backend Spring Boot
 */

/**
 * Request para crear un Payment Intent
 * Endpoint: POST /api/pagos/create-intent
 */
data class CreatePaymentIntentRequestDto(
    val amount: Int,            // Monto en CLP (pesos chilenos)
    val currency: String = "clp",
    val idPedido: Int
)

/**
 * Response del Payment Intent
 */
data class PaymentIntentResponseDto(
    val clientSecret: String,
    val paymentIntentId: String,
    val amount: Int,
    val currency: String
)

/**
 * Request para procesar un pago
 * Endpoint: POST /api/pagos/{pagoId}/process
 */
data class ProcessPaymentRequestDto(
    val paymentIntentId: String,
    val paymentMethodId: String
)

/**
 * Response completo de un pago
 */
data class PagoResponseDto(
    val id: Int,
    val pedido: PedidoResponseDto,
    val monto: Int,
    val estado: String,         // PENDIENTE, COMPLETADO, FALLIDO
    val metodoPago: String,
    val stripePaymentIntentId: String?,
    val fechaPago: String?,
    val detalles: String?
)

/**
 * Request para confirmar un pago
 * Endpoint: POST /api/pagos/confirm
 */
data class ConfirmPaymentRequestDto(
    val paymentIntentId: String
)
