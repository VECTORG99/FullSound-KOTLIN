package com.grupo8.fullsound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.remote.dto.AuthResponseDto
import com.grupo8.fullsound.data.remote.dto.MessageResponseDto
import com.grupo8.fullsound.repository.api.ApiAuthRepository
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.FormValidator
import kotlinx.coroutines.launch

/**
 * ViewModel para Login usando API REST del backend Spring Boot
 */
class ApiLoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = ApiAuthRepository(application.applicationContext)

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _loginResult = MutableLiveData<Resource<AuthResponseDto>>()
    val loginResult: LiveData<Resource<AuthResponseDto>> = _loginResult

    /**
     * Realizar login con el backend
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Resource.Loading()
            val result = authRepository.login(email, password)
            _loginResult.value = result
        }
    }

    /**
     * Validar formulario de login
     */
    fun validateForm(emailOrUsername: String, password: String) {
        val emailOrUsernameValid = FormValidator.isValidEmailOrUsername(emailOrUsername)
        val passwordValid = FormValidator.isValidPassword(password)

        _loginFormState.value = LoginFormState(
            emailError = if (!emailOrUsernameValid) "Email o usuario inválido" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 5 caracteres" else null,
            isDataValid = emailOrUsernameValid && passwordValid
        )
    }

    /**
     * Verificar si hay un usuario autenticado
     */
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    /**
     * Obtener datos del usuario actual
     */
    fun getCurrentUserData(): Map<String, Any?> {
        return authRepository.getCurrentUserData()
    }

    /**
     * Cerrar sesión
     */
    fun logout() {
        authRepository.logout()
    }
}

