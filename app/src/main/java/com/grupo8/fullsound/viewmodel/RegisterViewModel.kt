package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.model.User
import com.grupo8.fullsound.repository.UserRepository
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.FormValidator

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerFormState = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerFormState

    val registerResult: LiveData<Resource<User>> = userRepository.registerResult

    fun register(email: String, username: String, password: String) {
        userRepository.register(email, username, password, username)
    }

    fun validateForm(email: String, username: String, password: String) {
        val emailValid = FormValidator.isValidEmailStrict(email)
        val usernameValid = FormValidator.isValidUsername(username)
        val passwordValid = FormValidator.isValidPassword(password)

        _registerFormState.value = RegisterFormState(
            emailError = if (!emailValid) "Email inválido. Debe tener un dominio (ej: @gmail.com)" else null,
            usernameError = if (!usernameValid) "El usuario no puede estar vacío" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 5 caracteres" else null,
            isDataValid = emailValid && usernameValid && passwordValid
        )
    }
}

data class RegisterFormState(
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)

