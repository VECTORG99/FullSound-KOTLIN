package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.model.User
import com.grupo8.fullsound.repository.UserRepository
import com.grupo8.fullsound.utils.Resource

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerFormState = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerFormState

    val registerResult: LiveData<Resource<User>> = userRepository.registerResult

    fun register(email: String, username: String, password: String) {
        userRepository.register(email, username, password, username)
    }

    fun validateForm(email: String, username: String, password: String) {
        val emailValid = isValidEmail(email)
        val usernameValid = isValidUsername(username)
        val passwordValid = isPasswordValid(password)

        _registerFormState.value = RegisterFormState(
            emailError = if (!emailValid) "Email inválido. Debe tener un dominio (ej: @gmail.com)" else null,
            usernameError = if (!usernameValid) "El usuario no puede estar vacío" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 5 caracteres" else null,
            isDataValid = emailValid && usernameValid && passwordValid
        )
    }

    private fun isValidEmail(email: String): Boolean {
        // Verifica email
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            && email.contains("@")
            && email.substringAfter("@").contains(".")
    }

    private fun isValidUsername(username: String): Boolean {
        // Verifica que no esté vacío
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        // Mínimo 5 caracteres en contraseña
        return password.length >= 5
    }
}

data class RegisterFormState(
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)

