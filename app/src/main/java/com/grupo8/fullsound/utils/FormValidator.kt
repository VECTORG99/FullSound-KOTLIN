package com.grupo8.fullsound.utils

/**
 * Validador de formularios sin dependencias de Android.
 * Puede ser usado en tests unitarios JVM.
 */
object FormValidator {

    /**
     * Valida si un string es un email válido usando regex.
     * No depende de android.util.Patterns
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        // Regex simplificado para validar email
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    /**
     * Valida si un string es un email válido O un username válido (no vacío)
     */
    fun isValidEmailOrUsername(emailOrUsername: String): Boolean {
        if (emailOrUsername.isBlank()) return false

        // Es válido si es un email válido O si simplemente no está vacío (username)
        return isValidEmail(emailOrUsername) || emailOrUsername.isNotBlank()
    }

    /**
     * Valida si un username es válido (no vacío)
     */
    fun isValidUsername(username: String): Boolean {
        return username.isNotBlank()
    }

    /**
     * Valida si una password es válida (mínimo 5 caracteres)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 5
    }

    /**
     * Valida email con requisitos específicos:
     * - Debe contener @
     * - Debe tener un dominio después del @
     * - El dominio debe contener un punto
     */
    fun isValidEmailStrict(email: String): Boolean {
        if (email.isBlank()) return false
        if (!email.contains("@")) return false

        val parts = email.split("@")
        if (parts.size != 2) return false

        val localPart = parts[0]
        val domain = parts[1]

        // Verificar que ambas partes no estén vacías
        if (localPart.isBlank() || domain.isBlank()) return false

        // Verificar que el dominio contenga un punto
        if (!domain.contains(".")) return false

        // Verificar que haya algo después del punto
        val domainParts = domain.split(".")
        if (domainParts.any { it.isBlank() }) return false

        return true
    }
}

