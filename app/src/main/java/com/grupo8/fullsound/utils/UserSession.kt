package com.grupo8.fullsound.utils

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "FullSoundSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USERNAME = "user_username"
        private const val KEY_NAME = "user_name"
        private const val KEY_RUT = "user_rut"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Guardar sesión completa del usuario
    fun saveUserSession(userId: String, email: String, username: String, name: String, rut: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_USERNAME, username)
            putString(KEY_NAME, name)
            putString(KEY_RUT, rut)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Obtener ID del usuario
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    // Obtener email del usuario logueado
    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)

    // Obtener username del usuario
    fun getUserUsername(): String? = prefs.getString(KEY_USERNAME, null)

    // Obtener nombre del usuario
    fun getUserName(): String? = prefs.getString(KEY_NAME, null)

    // Obtener RUT del usuario
    fun getUserRut(): String? = prefs.getString(KEY_RUT, null)

    // Verificar si hay sesión activa
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    // Indica si el usuario es admin según el dominio del email
    fun isAdmin(): Boolean {
        val email = getUserEmail()
        return email?.endsWith("@admin.cl", ignoreCase = true) == true
    }

    // Cerrar sesión
    fun logout() {
        prefs.edit().clear().apply()
    }
}
