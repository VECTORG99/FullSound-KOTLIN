package com.grupo8.fullsound.ui.carrito

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.models.Beat
import com.grupo8.fullsound.data.models.CarritoItem
import com.grupo8.fullsound.data.repositories.CarritoRepository
import kotlinx.coroutines.launch

class CarritoViewModel(private val carritoRepository: CarritoRepository) : ViewModel() {

    val carritoItems: LiveData<List<CarritoItem>> = carritoRepository.carritoItems.asLiveData()

    fun addBeatToCarrito(beat: Beat) {
        viewModelScope.launch {
            carritoRepository.addBeatToCarrito(beat)
        }
    }

    fun removeItemFromCarrito(item: CarritoItem) {
        viewModelScope.launch {
            carritoRepository.removeItemFromCarrito(item)
        }
    }

    fun updateItemQuantity(item: CarritoItem, newQuantity: Int) {
        viewModelScope.launch {
            carritoRepository.updateItemQuantity(item, newQuantity)
        }
    }

    fun clearCarrito() {
        viewModelScope.launch {
            carritoRepository.clearCarrito()
        }
    }

    suspend fun getTotalPrice(): Double {
        return carritoRepository.getTotalPrice()
    }
}

