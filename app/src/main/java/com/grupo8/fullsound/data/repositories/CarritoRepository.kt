package com.grupo8.fullsound.data.repositories

import com.grupo8.fullsound.data.local.CarritoDao
import com.grupo8.fullsound.data.models.Beat
import com.grupo8.fullsound.data.models.CarritoItem
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val carritoDao: CarritoDao) {

    val carritoItems: Flow<List<CarritoItem>> = carritoDao.getAllItems()

    suspend fun addBeatToCarrito(beat: Beat) {
        // Verificar si el beat ya está en el carrito
        val existingItem = carritoDao.getItemByBeatId(beat.id)

        if (existingItem != null) {
            // Si ya existe, incrementar cantidad
            val updatedItem = existingItem.copy(cantidad = existingItem.cantidad + 1)
            carritoDao.updateItem(updatedItem)
        } else {
            // Si no existe, crear nuevo item
            val newItem = CarritoItem(
                beatId = beat.id,
                titulo = beat.titulo,
                artista = beat.artista,
                precio = beat.precio,
                imagenPath = beat.imagenPath,
                cantidad = 1
            )
            carritoDao.insertItem(newItem)
        }
    }

    suspend fun removeItemFromCarrito(item: CarritoItem) {
        carritoDao.deleteItem(item)
    }

    suspend fun updateItemQuantity(item: CarritoItem, newQuantity: Int) {
        if (newQuantity > 0) {
            val updatedItem = item.copy(cantidad = newQuantity)
            carritoDao.updateItem(updatedItem)
        } else {
            carritoDao.deleteItem(item)
        }
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

