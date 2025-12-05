package com.grupo8.fullsound.data.remote.api

import com.grupo8.fullsound.data.remote.dto.*
import retrofit2.http.*

/**
 * API Service para Pagos (Stripe Integration)
 * Base URL: /api/pagos
 */
interface PagoApiService {
    
    /**
     * Crear un Payment Intent de Stripe
     * POST /api/pagos/create-intent
     */
    @POST("pagos/create-intent")
    suspend fun createPaymentIntent(
        @Body request: CreatePaymentIntentRequestDto
    ): PaymentIntentResponseDto
    
    /**
     * Procesar un pago
     * POST /api/pagos/{pagoId}/process
     */
    @POST("pagos/{pagoId}/process")
    suspend fun processPayment(
        @Path("pagoId") pagoId: Int,
        @Body request: ProcessPaymentRequestDto
    ): PagoResponseDto
    
    /**
     * Obtener información de un pago por ID
     * GET /api/pagos/{id}
     */
    @GET("pagos/{id}")
    suspend fun getPagoById(
        @Path("id") id: Int
    ): PagoResponseDto
    
    /**
     * Confirmar un pago
     * POST /api/pagos/confirm
     */
    @POST("pagos/confirm")
    suspend fun confirmPayment(
        @Body request: ConfirmPaymentRequestDto
    ): PagoResponseDto
}
