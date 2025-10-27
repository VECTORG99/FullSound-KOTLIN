package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.repository.UserRepository
import com.grupo8.fullsound.model.User
import com.grupo8.fullsound.utils.Resource

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    val loginResult: LiveData<Resource<User>> = userRepository.loginResult

    fun login(emailOrUsername: String, password: String) {
        userRepository.login(emailOrUsername, password)
    }

    fun validateForm(emailOrUsername: String, password: String) {
        val emailOrUsernameValid = isValidEmailOrUsername(emailOrUsername)
        val passwordValid = isPasswordValid(password)

        _loginFormState.value = LoginFormState(
            emailError = if (!emailOrUsernameValid) "Email o usuario inválido" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 5 caracteres" else null,
            isDataValid = emailOrUsernameValid && passwordValid
        )
    }

    private fun isValidEmailOrUsername(emailOrUsername: String): Boolean {
        // Válido si es un email válido O si no está vacío (username)
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()
            || emailOrUsername.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 5
    }
}

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)

