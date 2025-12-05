package com.grupo8.fullsound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.remote.dto.MessageResponseDto
import com.grupo8.fullsound.data.remote.dto.UsuarioDto
import com.grupo8.fullsound.repository.api.ApiUsuarioRepository
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.launch

/**
 * ViewModel para Usuarios usando API REST del backend Spring Boot
 */
class ApiUsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = ApiUsuarioRepository(application.applicationContext)

    private val _currentUser = MutableLiveData<Resource<UsuarioDto>>()
    val currentUser: LiveData<Resource<UsuarioDto>> = _currentUser

    private val _usuario = MutableLiveData<Resource<UsuarioDto>>()
    val usuario: LiveData<Resource<UsuarioDto>> = _usuario

    private val _allUsers = MutableLiveData<Resource<List<UsuarioDto>>>()
    val allUsers: LiveData<Resource<List<UsuarioDto>>> = _allUsers

    private val _changePasswordResult = MutableLiveData<Resource<MessageResponseDto>>()
    val changePasswordResult: LiveData<Resource<MessageResponseDto>> = _changePasswordResult

    /**
     * Obtener información del usuario autenticado
     */
    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = Resource.Loading()
            val result = usuarioRepository.getCurrentUser()
            _currentUser.value = result
        }
    }

    /**
     * Obtener un usuario por ID
     */
    fun getUserById(id: Int) {
        viewModelScope.launch {
            _usuario.value = Resource.Loading()
            val result = usuarioRepository.getUserById(id)
            _usuario.value = result
        }
    }

    /**
     * Obtener todos los usuarios (requiere ADMIN)
     */
    fun getAllUsers() {
        viewModelScope.launch {
            _allUsers.value = Resource.Loading()
            val result = usuarioRepository.getAllUsers()
            _allUsers.value = result
        }
    }

    /**
     * Cambiar contraseña del usuario autenticado
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordResult.value = Resource.Loading()
            val result = usuarioRepository.changePassword(currentPassword, newPassword)
            _changePasswordResult.value = result
        }
    }
}
