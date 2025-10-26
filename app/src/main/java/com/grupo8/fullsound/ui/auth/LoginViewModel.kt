package com.grupo8.fullsound.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.repositories.UserRepository
import com.grupo8.fullsound.data.models.User
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    val loginResult: LiveData<Resource<User>> = userRepository.loginResult

    fun login(email: String, password: String) {
        userRepository.login(email, password)
    }

    fun validateForm(email: String, password: String) {
        val emailValid = isValidEmail(email)
        val passwordValid = isPasswordValid(password)

        _loginFormState.value = LoginFormState(
            emailError = if (!emailValid) "Correo electrónico inválido" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 6 caracteres" else null,
            isDataValid = emailValid && passwordValid
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)