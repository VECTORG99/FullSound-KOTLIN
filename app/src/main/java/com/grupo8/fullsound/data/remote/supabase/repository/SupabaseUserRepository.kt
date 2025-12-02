package com.grupo8.fullsound.data.remote.supabase.repository

import android.util.Log
import com.grupo8.fullsound.data.remote.supabase.SupabaseClient
import com.grupo8.fullsound.data.remote.supabase.dto.UserSupabaseDto
import com.grupo8.fullsound.model.User
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones CRUD de Users en Supabase
 * Adaptado a la tabla 'usuario' de Supabase
 */
class SupabaseUserRepository {

    private val TAG = "SupabaseUserRepository"
    private val supabase = SupabaseClient.client

    /**
     * Convierte UserSupabaseDto a modelo User local
     */
    private fun UserSupabaseDto.toModel(): User {
        return User(
            id = this.idUsuario?.toString() ?: "0",
            email = this.correo,
            username = this.nombreUsuario,
            password = this.contrasena,
            name = this.nombre ?: this.nombreUsuario,
            role = when (this.idRol) {
                1 -> "admin"
                2 -> "productor"
                else -> "user"
            },
            profileImage = null,
            createdAt = System.currentTimeMillis()
        )
    }

    /**
     * Convierte modelo User local a UserSupabaseDto
     */
    private fun User.toDto(): UserSupabaseDto {
        return UserSupabaseDto(
            idUsuario = if (this.id != "0") this.id.toIntOrNull() else null,
            nombreUsuario = this.username,
            correo = this.email,
            contrasena = this.password,
            nombre = this.name,
            apellido = null,
            activo = true,
            idRol = when (this.role.lowercase()) {
                "admin" -> 1
                "productor" -> 2
                else -> 3
            }
        )
    }

    /**
     * Obtiene todos los usuarios desde Supabase
     */
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Obteniendo todos los usuarios desde Supabase...")
            val response = supabase
                .from("usuario")
                .select()
                .decodeList<UserSupabaseDto>()

            Log.d(TAG, "✅ ${response.size} usuarios obtenidos")
            response.map { it.toModel() }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener usuarios", e)
            emptyList()
        }
    }

    /**
     * Obtiene un usuario por ID desde Supabase
     */
    suspend fun getUserById(id: String): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Buscando usuario con ID: $id")
            val response = supabase
                .from("usuario")
                .select {
                    filter {
                        eq("id_usuario", id.toIntOrNull() ?: 0)
                    }
                }
                .decodeSingleOrNull<UserSupabaseDto>()

            response?.toModel().also {
                if (it != null) Log.d(TAG, "✅ Usuario encontrado: ${it.username}")
                else Log.w(TAG, "⚠️ Usuario con ID $id no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener usuario por ID", e)
            null
        }
    }

    /**
     * Obtiene un usuario por email desde Supabase
     */
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Buscando usuario con correo: $email")
            val response = supabase
                .from("usuario")
                .select {
                    filter {
                        eq("correo", email)
                    }
                }
                .decodeSingleOrNull<UserSupabaseDto>()

            response?.toModel().also {
                if (it != null) Log.d(TAG, "✅ Usuario encontrado: ${it.username}")
                else Log.w(TAG, "⚠️ Usuario con correo $email no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener usuario por email", e)
            null
        }
    }

    /**
     * Obtiene un usuario por username desde Supabase
     */
    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Buscando usuario con nombre_usuario: $username")
            val response = supabase
                .from("usuario")
                .select {
                    filter {
                        eq("nombre_usuario", username)
                    }
                }
                .decodeSingleOrNull<UserSupabaseDto>()

            response?.toModel().also {
                if (it != null) Log.d(TAG, "✅ Usuario encontrado: ${it.username}")
                else Log.w(TAG, "⚠️ Usuario con nombre_usuario $username no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener usuario por username", e)
            null
        }
    }

    /**
     * Obtiene un usuario por email/username y password (para login)
     */
    suspend fun getUserByEmailOrUsername(emailOrUsername: String, password: String): User? =
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Intentando login con: $emailOrUsername")
            val response = supabase
                .from("usuario")
                .select {
                    filter {
                        or {
                            eq("correo", emailOrUsername)
                            eq("nombre_usuario", emailOrUsername)
                        }
                        eq("contraseña", password)
                    }
                }
                .decodeSingleOrNull<UserSupabaseDto>()

            response?.toModel().also {
                if (it != null) Log.d(TAG, "✅ Login exitoso: ${it.username}")
                else Log.w(TAG, "⚠️ Credenciales incorrectas")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en login", e)
            null
        }
    }

    /**
     * Inserta un nuevo usuario en Supabase
     */
    suspend fun insertUser(user: User): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Insertando usuario: ${user.username}")
            val dto = user.toDto()
            val response = supabase
                .from("usuario")
                .insert(dto) {
                    select()
                }
                .decodeSingle<UserSupabaseDto>()

            response.toModel().also {
                Log.d(TAG, "✅ Usuario insertado con ID: ${it.id}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al insertar usuario", e)
            null
        }
    }

    /**
     * Actualiza un usuario existente en Supabase
     */
    suspend fun updateUser(user: User): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Actualizando usuario ID: ${user.id}")
            val dto = user.toDto()
            val response = supabase
                .from("usuario")
                .update(dto) {
                    filter {
                        eq("id_usuario", user.id.toIntOrNull() ?: 0)
                    }
                    select()
                }
                .decodeSingle<UserSupabaseDto>()

            response.toModel().also {
                Log.d(TAG, "✅ Usuario actualizado: ${it.username}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al actualizar usuario", e)
            null
        }
    }

    /**
     * Elimina un usuario de Supabase
     */
    suspend fun deleteUser(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Eliminando usuario ID: $id")
            supabase
                .from("usuario")
                .delete {
                    filter {
                        eq("id_usuario", id.toIntOrNull() ?: 0)
                    }
                }
            Log.d(TAG, "✅ Usuario eliminado")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al eliminar usuario", e)
            false
        }
    }
}

