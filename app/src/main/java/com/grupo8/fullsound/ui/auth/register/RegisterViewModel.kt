package com.grupo8.fullsound.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.data.models.User
import com.grupo8.fullsound.data.repositories.UserRepository
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
            emailError = if (!emailValid) "Correo electrónico inválido" else null,
            usernameError = if (!usernameValid) "El usuario debe tener al menos 3 caracteres" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 6 caracteres" else null,
            isDataValid = emailValid && usernameValid && passwordValid
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length >= 3 && username.matches(Regex("^[a-zA-Z0-9_]+$"))
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}

data class RegisterFormState(
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)
