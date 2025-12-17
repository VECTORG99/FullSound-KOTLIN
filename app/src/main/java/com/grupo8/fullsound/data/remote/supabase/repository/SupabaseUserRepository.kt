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
     * Si el correo termina en @admin.cl, fuerza el rol a admin
     */
    private fun UserSupabaseDto.toModel(): User {
        // Determinar el rol basado en el id_rol
        val roleFromId = when (this.idRol) {
            1 -> "admin"
            2 -> "productor"
            else -> "user"
        }

        // Si el correo termina en @admin.cl, forzar rol admin
        val finalRole = if (this.correo.lowercase().endsWith("@admin.cl")) {
            "admin"
        } else {
            roleFromId
        }

        return User(
            id = this.idUsuario?.toString() ?: "0",
            email = this.correo,
            username = this.nombreUsuario,
            password = this.contrasena,
            name = this.nombre ?: this.nombreUsuario,
            rut = this.rut ?: "",
            role = finalRole,
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
            },
            rut = this.rut
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
            Log.d(TAG, " Buscando usuario con ID: $id")
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
                else Log.w(TAG, " Usuario con ID $id no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Error al obtener usuario por ID", e)
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
                if (it != null) Log.d(TAG, " Usuario encontrado: ${it.username}")
                else Log.w(TAG, " Usuario con correo $email no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Error al obtener usuario por email", e)
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
            Log.d(TAG, "========== INTENTO DE LOGIN ==========")
            Log.d(TAG, "📡 Datos del intento:")
            Log.d(TAG, "   Email/Username: $emailOrUsername")
            Log.d(TAG, "   Password length: ${password.length} caracteres")
            Log.d(TAG, "   Buscando en tabla 'usuario' de Supabase...")

            // Primero, intentar obtener el usuario sin verificar contraseña para debug
            val userWithoutPassword = supabase
                .from("usuario")
                .select {
                    filter {
                        or {
                            eq("correo", emailOrUsername)
                            eq("nombre_usuario", emailOrUsername)
                        }
                    }
                }
                .decodeSingleOrNull<UserSupabaseDto>()

            if (userWithoutPassword != null) {
                Log.d(TAG, "✅ Usuario encontrado en BD:")
                Log.d(TAG, "   - ID: ${userWithoutPassword.idUsuario}")
                Log.d(TAG, "   - Username: ${userWithoutPassword.nombreUsuario}")
                Log.d(TAG, "   - Email: ${userWithoutPassword.correo}")
                Log.d(TAG, "   - Rol ID: ${userWithoutPassword.idRol}")
                Log.d(TAG, "   - Activo: ${userWithoutPassword.activo}")
                Log.d(TAG, "   - Password en BD length: ${userWithoutPassword.contrasena.length}")

                // Verificar contraseña
                if (userWithoutPassword.contrasena == password) {
                    Log.d(TAG, "✅ Contraseña correcta - Login exitoso")
                    val user = userWithoutPassword.toModel()
                    Log.d(TAG, "   Rol asignado: ${user.role}")
                    Log.d(TAG, "======================================")
                    return@withContext user
                } else {
                    Log.w(TAG, "❌ Contraseña incorrecta")
                    Log.w(TAG, "   Password ingresado: '$password'")
                    Log.w(TAG, "   Password en BD: '${userWithoutPassword.contrasena}'")
                    Log.w(TAG, "   ¿Coinciden? ${userWithoutPassword.contrasena == password}")
                    Log.w(TAG, "======================================")
                    return@withContext null
                }
            } else {
                Log.w(TAG, "❌ Usuario NO encontrado en BD")
                Log.w(TAG, "   Buscado por: $emailOrUsername")
                Log.w(TAG, "   Verifica que el usuario existe en Supabase")
                Log.w(TAG, "======================================")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR en login: ${e.message}", e)
            Log.e(TAG, "   Tipo de error: ${e.javaClass.simpleName}")
            Log.e(TAG, "   Stack trace:")
            e.printStackTrace()
            Log.e(TAG, "======================================")
            null
        }
    }

    /**
     * Inserta un nuevo usuario en Supabase
     * Si el correo termina en @admin.cl, asigna rol de administrador automáticamente
     */
    suspend fun insertUser(user: User): User? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 Insertando usuario: ${user.username} (${user.email})")

            // Determinar el rol basado en el correo
            val isAdmin = user.email.lowercase().endsWith("@admin.cl")
            val assignedRole = if (isAdmin) "admin" else user.role
            val idRol = if (isAdmin) 1 else when (user.role.lowercase()) {
                "admin" -> 1
                "productor" -> 2
                else -> 3
            }

            Log.d(TAG, "   Email: ${user.email}")
            Log.d(TAG, "   Es admin: $isAdmin")
            Log.d(TAG, "   Rol asignado: $assignedRole (id_rol: $idRol)")

            // Crear DTO con el rol correcto
            val dto = UserSupabaseDto(
                idUsuario = null, // Supabase lo genera automáticamente
                nombreUsuario = user.username,
                correo = user.email,
                contrasena = user.password,
                nombre = user.name,
                apellido = null,
                activo = true,
                idRol = idRol,
                rut = user.rut
            )

            val response = supabase
                .from("usuario")
                .insert(dto) {
                    select()
                }
                .decodeSingle<UserSupabaseDto>()

            response.toModel().also {
                Log.d(TAG, "Usuario insertado con ID: ${it.id}, Rol: ${it.role}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar usuario: ${e.message}", e)
            Log.e(TAG, "   Tipo de error: ${e.javaClass.simpleName}")
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

