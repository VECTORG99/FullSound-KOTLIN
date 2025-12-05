package com.grupo8.fullsound.viewmodel

/**
 * Estados de formularios para validación
 */

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)

data class RegisterFormState(
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val rutError: String? = null,
    val confirmPasswordError: String? = null,
    val isDataValid: Boolean = false
)

