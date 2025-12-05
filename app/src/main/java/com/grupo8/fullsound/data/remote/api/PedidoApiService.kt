package com.grupo8.fullsound.data.remote.api

import com.grupo8.fullsound.data.remote.dto.*
import retrofit2.http.*

/**
 * API Service para Pedidos
 * Base URL: /api/pedidos
 */
interface PedidoApiService {
    
    /**
     * Crear un nuevo pedido
     * POST /api/pedidos
     */
    @POST("pedidos")
    suspend fun createPedido(
        @Body request: PedidoRequestDto
    ): PedidoResponseDto
    
    /**
     * Obtener un pedido por ID
     * GET /api/pedidos/{id}
     */
    @GET("pedidos/{id}")
    suspend fun getPedidoById(
        @Path("id") id: Int
    ): PedidoResponseDto
    
    /**
     * Obtener un pedido por número de pedido
     * GET /api/pedidos/numero/{numeroPedido}
     */
    @GET("pedidos/numero/{numeroPedido}")
    suspend fun getPedidoByNumero(
        @Path("numeroPedido") numero: String
    ): PedidoResponseDto
    
    /**
     * Obtener todos los pedidos del usuario autenticado
     * GET /api/pedidos/mis-pedidos
     */
    @GET("pedidos/mis-pedidos")
    suspend fun getMisPedidos(): List<PedidoResponseDto>
    
    /**
     * Obtener todos los pedidos (requiere rol ADMIN)
     * GET /api/pedidos
     */
    @GET("pedidos")
    suspend fun getAllPedidos(): List<PedidoResponseDto>
    
    /**
     * Actualizar el estado de un pedido (requiere rol ADMIN)
     * PATCH /api/pedidos/{id}/estado?estado={nuevo_estado}
     */
    @PATCH("pedidos/{id}/estado")
    suspend fun updateEstadoPedido(
        @Path("id") id: Int,
        @Query("estado") estado: String
    ): PedidoResponseDto
}
