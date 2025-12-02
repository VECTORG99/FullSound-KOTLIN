package com.grupo8.fullsound.data.remote.supabase.repository

import com.grupo8.fullsound.data.remote.supabase.SupabaseClient
import com.grupo8.fullsound.data.remote.supabase.dto.CarritoItemSupabaseDto
import com.grupo8.fullsound.model.CarritoItem
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones CRUD de CarritoItems en Supabase
 */
class SupabaseCarritoRepository {

    private val supabase = SupabaseClient.client

    /**
     * Obtiene todos los items del carrito de un usuario desde Supabase
     */
    suspend fun getCarritoItemsByUserId(userId: String): List<CarritoItemSupabaseDto> =
        withContext(Dispatchers.IO) {
        try {
            val response = supabase
                .from("carrito_items")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<CarritoItemSupabaseDto>()

            response
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Obtiene un item del carrito por ID desde Supabase
     */
    suspend fun getCarritoItemById(id: Int): CarritoItemSupabaseDto? = withContext(Dispatchers.IO) {
        try {
            val response = supabase
                .from("carrito_items")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<CarritoItemSupabaseDto>()

            response
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Inserta un nuevo item en el carrito en Supabase
     */
    suspend fun insertCarritoItem(userId: String, beatId: Int, cantidad: Int, precioUnitario: Double): CarritoItemSupabaseDto? =
        withContext(Dispatchers.IO) {
        try {
            val dto = CarritoItemSupabaseDto(
                userId = userId,
                beatId = beatId,
                cantidad = cantidad,
                precioUnitario = precioUnitario
            )
            val response = supabase
                .from("carrito_items")
                .insert(dto)
                .decodeSingle<CarritoItemSupabaseDto>()

            response
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Actualiza la cantidad de un item en el carrito en Supabase
     */
    suspend fun updateCarritoItemCantidad(id: Int, cantidad: Int): Boolean =
        withContext(Dispatchers.IO) {
        try {
            supabase
                .from("carrito_items")
                .update({
                    set("cantidad", cantidad)
                }) {
                    filter {
                        eq("id", id)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Elimina un item del carrito de Supabase
     */
    suspend fun deleteCarritoItem(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase
                .from("carrito_items")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Elimina todos los items del carrito de un usuario
     */
    suspend fun deleteAllCarritoItemsByUserId(userId: String): Boolean =
        withContext(Dispatchers.IO) {
        try {
            supabase
                .from("carrito_items")
                .delete {
                    filter {
                        eq("user_id", userId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

