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
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Guardar sesión del usuario
    fun saveUserSession(userId: Int, email: String, username: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Obtener ID del usuario logueado
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    // Obtener email del usuario logueado
    fun getUserEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    // Obtener username del usuario logueado
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    // Verificar si hay sesión activa
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Cerrar sesión
    fun logout() {
        prefs.edit().clear().apply()
    }

    // Limpiar sesión
    fun clearSession() {
        logout()
    }
}

