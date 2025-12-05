package com.grupo8.fullsound.repository.api

import android.content.Context
import android.util.Log
import com.grupo8.fullsound.data.remote.RetrofitClient
import com.grupo8.fullsound.data.remote.dto.PedidoRequestDto
import com.grupo8.fullsound.data.remote.dto.PedidoResponseDto
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones con Pedidos usando el backend Spring Boot
 */
class ApiPedidoRepository(private val context: Context) {
    
    private val TAG = "ApiPedidoRepository"
    private val pedidoApiService = RetrofitClient.getPedidoApiService(context)
    
    /**
     * Crear un nuevo pedido
     */
    suspend fun createPedido(pedido: PedidoRequestDto): Resource<PedidoResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Creando pedido con ${pedido.items.size} items")
                val createdPedido = pedidoApiService.createPedido(pedido)
                Log.d(TAG, "✅ Pedido creado: ${createdPedido.numeroPedido}")
                Resource.Success(createdPedido)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al crear pedido", e)
                Resource.Error("Error al crear pedido: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener un pedido por ID
     */
    suspend fun getPedidoById(id: Int): Resource<PedidoResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo pedido ID: $id")
                val pedido = pedidoApiService.getPedidoById(id)
                Log.d(TAG, "✅ Pedido obtenido: ${pedido.numeroPedido}")
                Resource.Success(pedido)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener pedido", e)
                Resource.Error("Error al cargar pedido: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener un pedido por número de pedido
     */
    suspend fun getPedidoByNumero(numero: String): Resource<PedidoResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo pedido número: $numero")
                val pedido = pedidoApiService.getPedidoByNumero(numero)
                Log.d(TAG, "✅ Pedido obtenido: ${pedido.numeroPedido}")
                Resource.Success(pedido)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener pedido por número", e)
                Resource.Error("Error al cargar pedido: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener todos los pedidos del usuario autenticado
     */
    suspend fun getMisPedidos(): Resource<List<PedidoResponseDto>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo mis pedidos...")
                val pedidos = pedidoApiService.getMisPedidos()
                Log.d(TAG, "✅ ${pedidos.size} pedidos obtenidos")
                Resource.Success(pedidos)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener mis pedidos", e)
                Resource.Error("Error al cargar pedidos: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener todos los pedidos (requiere rol ADMIN)
     */
    suspend fun getAllPedidos(): Resource<List<PedidoResponseDto>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo todos los pedidos (admin)...")
                val pedidos = pedidoApiService.getAllPedidos()
                Log.d(TAG, "✅ ${pedidos.size} pedidos obtenidos")
                Resource.Success(pedidos)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener todos los pedidos", e)
                Resource.Error("Error al cargar pedidos: ${e.message}")
            }
        }
    }
    
    /**
     * Actualizar el estado de un pedido (requiere rol ADMIN)
     * Estados posibles: PENDIENTE, CONFIRMADO, CANCELADO, COMPLETADO
     */
    suspend fun updateEstadoPedido(id: Int, nuevoEstado: String): Resource<PedidoResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Actualizando estado del pedido $id a $nuevoEstado")
                val pedido = pedidoApiService.updateEstadoPedido(id, nuevoEstado)
                Log.d(TAG, "✅ Estado actualizado: ${pedido.estado}")
                Resource.Success(pedido)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al actualizar estado del pedido", e)
                Resource.Error("Error al actualizar pedido: ${e.message}")
            }
        }
    }
}
