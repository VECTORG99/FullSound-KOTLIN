package com.grupo8.fullsound.data.remote.interceptor

import android.content.Context
import android.util.Log

/**
 * Gestor de tokens JWT para almacenar y recuperar el token de autenticación
 */
class TokenManager(context: Context) {
    
    companion object {
        private const val AUTH_PREFS = "auth_prefs"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val TAG = "TokenManager"
    }
    
    private val sharedPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
    
    /**
     * Guardar token JWT y datos del usuario
     */
    fun saveToken(token: String, userId: Int, username: String, email: String, role: String) {
        sharedPrefs.edit().apply {
            putString(KEY_JWT_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            apply()
        }
        Log.d(TAG, "Token guardado para usuario: $username")
    }
    
    /**
     * Obtener token JWT almacenado
     */
    fun getToken(): String? {
        return sharedPrefs.getString(KEY_JWT_TOKEN, null)
    }
    
    /**
     * Obtener ID del usuario
     */
    fun getUserId(): Int {
        return sharedPrefs.getInt(KEY_USER_ID, -1)
    }
    
    /**
     * Obtener nombre de usuario
     */
    fun getUsername(): String? {
        return sharedPrefs.getString(KEY_USERNAME, null)
    }
    
    /**
     * Obtener email del usuario
     */
    fun getUserEmail(): String? {
        return sharedPrefs.getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Obtener rol del usuario
     */
    fun getUserRole(): String? {
        return sharedPrefs.getString(KEY_USER_ROLE, null)
    }
    
    /**
     * Verificar si hay un usuario autenticado
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null && getUserId() != -1
    }
    
    /**
     * Verificar si el usuario es administrador
     */
    fun isAdmin(): Boolean {
        return getUserRole()?.equals("ADMIN", ignoreCase = true) == true
    }
    
    /**
     * Limpiar token y datos del usuario (logout)
     */
    fun clearToken() {
        sharedPrefs.edit().clear().apply()
        Log.d(TAG, "Token y datos de usuario limpiados")
    }
}
