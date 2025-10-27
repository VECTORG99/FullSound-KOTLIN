package com.grupo8.fullsound.repository

import com.grupo8.fullsound.data.local.CarritoDao
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.model.CarritoItem
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val carritoDao: CarritoDao) {

    val carritoItems: Flow<List<CarritoItem>> = carritoDao.getAllItems()

    suspend fun addBeatToCarrito(beat: Beat): Boolean {
        // Verificar si el beat ya está en el carrito
        val existingItem = carritoDao.getItemByBeatId(beat.id)

        if (existingItem != null) {
            // Si ya existe, retornar false (no se permite duplicados)
            return false
        } else {
            // Si no existe, crear nuevo item con cantidad fija de 1
            val newItem = CarritoItem(
                beatId = beat.id,
                titulo = beat.titulo,
                artista = beat.artista,
                precio = beat.precio,
                imagenPath = beat.imagenPath,
                cantidad = 1  // Siempre 1, no se permite más
            )
            carritoDao.insertItem(newItem)
            return true
        }
    }

    suspend fun removeItemFromCarrito(item: CarritoItem) {
        carritoDao.deleteItem(item)
    }

    suspend fun clearCarrito() {
        carritoDao.deleteAllItems()
    }

    suspend fun getTotalPrice(): Double {
        return carritoDao.getTotalPrice() ?: 0.0
    }

    suspend fun getAllItemsList(): List<CarritoItem> {
        return carritoDao.getAllItemsList()
    }
}

