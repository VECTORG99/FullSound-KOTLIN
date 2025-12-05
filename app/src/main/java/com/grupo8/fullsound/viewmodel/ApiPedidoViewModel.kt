package com.grupo8.fullsound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.remote.dto.PedidoRequestDto
import com.grupo8.fullsound.data.remote.dto.PedidoResponseDto
import com.grupo8.fullsound.repository.api.ApiPedidoRepository
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.launch

/**
 * ViewModel para Pedidos usando API REST del backend Spring Boot
 */
class ApiPedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val pedidoRepository = ApiPedidoRepository(application.applicationContext)

    private val _pedidoResult = MutableLiveData<Resource<PedidoResponseDto>>()
    val pedidoResult: LiveData<Resource<PedidoResponseDto>> = _pedidoResult

    private val _misPedidos = MutableLiveData<Resource<List<PedidoResponseDto>>>()
    val misPedidos: LiveData<Resource<List<PedidoResponseDto>>> = _misPedidos

    private val _allPedidos = MutableLiveData<Resource<List<PedidoResponseDto>>>()
    val allPedidos: LiveData<Resource<List<PedidoResponseDto>>> = _allPedidos

    /**
     * Crear un nuevo pedido
     */
    fun createPedido(pedido: PedidoRequestDto) {
        viewModelScope.launch {
            _pedidoResult.value = Resource.Loading()
            val result = pedidoRepository.createPedido(pedido)
            _pedidoResult.value = result
        }
    }

    /**
     * Obtener un pedido por ID
     */
    fun getPedidoById(id: Int) {
        viewModelScope.launch {
            _pedidoResult.value = Resource.Loading()
            val result = pedidoRepository.getPedidoById(id)
            _pedidoResult.value = result
        }
    }

    /**
     * Obtener un pedido por número
     */
    fun getPedidoByNumero(numero: String) {
        viewModelScope.launch {
            _pedidoResult.value = Resource.Loading()
            val result = pedidoRepository.getPedidoByNumero(numero)
            _pedidoResult.value = result
        }
    }

    /**
     * Obtener todos los pedidos del usuario autenticado
     */
    fun getMisPedidos() {
        viewModelScope.launch {
            _misPedidos.value = Resource.Loading()
            val result = pedidoRepository.getMisPedidos()
            _misPedidos.value = result
        }
    }

    /**
     * Obtener todos los pedidos (requiere ADMIN)
     */
    fun getAllPedidos() {
        viewModelScope.launch {
            _allPedidos.value = Resource.Loading()
            val result = pedidoRepository.getAllPedidos()
            _allPedidos.value = result
        }
    }

    /**
     * Actualizar el estado de un pedido (requiere ADMIN)
     * Estados: PENDIENTE, CONFIRMADO, CANCELADO, COMPLETADO
     */
    fun updateEstadoPedido(id: Int, nuevoEstado: String) {
        viewModelScope.launch {
            _pedidoResult.value = Resource.Loading()
            val result = pedidoRepository.updateEstadoPedido(id, nuevoEstado)
            _pedidoResult.value = result
        }
    }
}
