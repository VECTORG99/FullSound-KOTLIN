package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.model.CarritoItem
import com.grupo8.fullsound.repository.CarritoRepository
import kotlinx.coroutines.launch

class CarritoViewModel(private val carritoRepository: CarritoRepository) : ViewModel() {

    val carritoItems: LiveData<List<CarritoItem>> = carritoRepository.carritoItems.asLiveData()

    private val _addToCarritoResult = MutableLiveData<AddToCarritoResult>()
    val addToCarritoResult: LiveData<AddToCarritoResult> = _addToCarritoResult

    fun addBeatToCarrito(beat: Beat) {
        viewModelScope.launch {
            val success = carritoRepository.addBeatToCarrito(beat)
            if (success) {
                _addToCarritoResult.value = AddToCarritoResult.Success(beat.titulo)
            } else {
                _addToCarritoResult.value = AddToCarritoResult.AlreadyExists(beat.titulo)
            }
        }
    }

    fun removeItemFromCarrito(item: CarritoItem) {
        viewModelScope.launch {
            carritoRepository.removeItemFromCarrito(item)
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

    suspend fun getSubtotal(): Double {
        return carritoRepository.getTotalPrice()
    }

    suspend fun getIva(): Double {
        val subtotal = getSubtotal()
        return com.grupo8.fullsound.utils.Constants.calcularIva(subtotal)
    }

    suspend fun getTotalConIva(): Double {
        val subtotal = getSubtotal()
        return com.grupo8.fullsound.utils.Constants.calcularTotalConIva(subtotal)
    }
}

sealed class AddToCarritoResult {
    data class Success(val beatTitle: String) : AddToCarritoResult()
    data class AlreadyExists(val beatTitle: String) : AddToCarritoResult()
}

