package com.grupo8.fullsound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.remote.dto.MessageResponseDto
import com.grupo8.fullsound.repository.api.ApiAuthRepository
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.FormValidator
import kotlinx.coroutines.launch

/**
 * ViewModel para Registro usando API REST del backend Spring Boot
 */
class ApiRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = ApiAuthRepository(application.applicationContext)

    private val _registerFormState = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerFormState

    private val _registerResult = MutableLiveData<Resource<MessageResponseDto>>()
    val registerResult: LiveData<Resource<MessageResponseDto>> = _registerResult

    /**
     * Registrar nuevo usuario
     */
    fun register(
        email: String,
        username: String,
        password: String,
        nombre: String? = null,
        apellido: String? = null
    ) {
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            val result = authRepository.register(
                username = username,
                email = email,
                password = password,
                nombre = nombre,
                apellido = apellido
            )
            _registerResult.value = result
        }
    }

    /**
     * Validar formulario de registro
     */
    fun validateForm(email: String, username: String, password: String, rut: String? = null) {
        val emailValid = FormValidator.isValidEmailStrict(email)
        val usernameValid = FormValidator.isValidUsername(username)
        val passwordValid = FormValidator.isValidPassword(password)
        val rutValid = rut?.let { com.grupo8.fullsound.utils.RutValidator.validarRut(it) } ?: true

        _registerFormState.value = RegisterFormState(
            emailError = if (!emailValid) "Email inválido. Debe tener un dominio (ej: @gmail.com)" else null,
            usernameError = if (!usernameValid) "El usuario no puede estar vacío" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 5 caracteres" else null,
            rutError = rut?.let { com.grupo8.fullsound.utils.RutValidator.obtenerMensajeError(it) },
            isDataValid = emailValid && usernameValid && passwordValid && rutValid
        )
    }
}

