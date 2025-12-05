package com.grupo8.fullsound.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.UserDao
import com.grupo8.fullsound.model.User
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import com.grupo8.fullsound.repository.api.ApiAuthRepository

class UserRepository(
    private val userDao: UserDao,
    private val context: Context,
    private val apiAuthRepo: ApiAuthRepository = ApiAuthRepository(context)
) {

    private val _loginResult = MutableLiveData<Resource<User>>()
    val loginResult: LiveData<Resource<User>> = _loginResult

    private val _registerResult = MutableLiveData<Resource<User>>()
    val registerResult: LiveData<Resource<User>> = _registerResult

    private val _userResult = MutableLiveData<Resource<User>>()
    val userResult: LiveData<Resource<User>> = _userResult

    private val _usersResult = MutableLiveData<Resource<List<User>>>()
    val usersResult: LiveData<Resource<List<User>>> = _usersResult

    private val _deleteResult = MutableLiveData<Resource<String>>()
    val deleteResult: LiveData<Resource<String>> = _deleteResult

    fun login(emailOrUsername: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _loginResult.postValue(Resource.Loading())
            try {
                // Intentar login desde backend Spring Boot primero
                try {
                    val response = apiAuthRepo.login(emailOrUsername, password)
                    if (response is Resource.Success && response.data != null) {
                        val authResponse = response.data
                        val user = User(
                            id = authResponse.usuario.id.toString(),
                            username = authResponse.usuario.nombreUsuario,
                            email = authResponse.usuario.correo,
                            password = "", // No guardar password
                            name = authResponse.usuario.nombre ?: "",
                            rut = "", // El backend no devuelve RUT en UsuarioDto
                            createdAt = System.currentTimeMillis()
                        )
                        // Guardar/actualizar en caché local
                        userDao.insertUser(user)
                        _loginResult.postValue(Resource.Success(user))
                        return@launch
                    }
                } catch (backendEx: Exception) {
                    android.util.Log.e("UserRepository", "Error en login backend", backendEx)
                }

                // Fallback a BD local
                val user = userDao.getUserByEmailOrUsername(emailOrUsername, password)
                if (user != null) {
                    _loginResult.postValue(Resource.Success(user))
                } else {
                    _loginResult.postValue(Resource.Error("Credenciales inválidas"))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Resource.Error("Error de conexión: ${e.message}"))
            }
        }
    }

    fun register(email: String, username: String, password: String, name: String, rut: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _registerResult.postValue(Resource.Loading())
            try {
                android.util.Log.d("UserRepository", "=== INICIANDO REGISTRO ===")
                android.util.Log.d("UserRepository", "Email: $email")
                android.util.Log.d("UserRepository", "Username: $username")
                android.util.Log.d("UserRepository", "Name: $name")
                android.util.Log.d("UserRepository", "RUT: $rut")

                // Intentar registrar en backend Spring Boot
                try {
                    android.util.Log.d("UserRepository", "Registrando usuario en backend Spring Boot...")
                    val response = apiAuthRepo.register(
                        username = username,
                        email = email,
                        password = password,
                        nombre = name,
                        apellido = null
                    )

                    when (response) {
                        is Resource.Success -> {
                            android.util.Log.d("UserRepository", "✅ Registro exitoso en backend")

                            // Crear usuario local
                            val newUser = User(
                                id = UUID.randomUUID().toString(), // Temporal hasta que se sincronice
                                username = username,
                                email = email,
                                password = password,
                                name = name,
                                rut = rut,
                                createdAt = System.currentTimeMillis()
                            )

                            // Guardar en BD local
                            userDao.insertUser(newUser)
                            _registerResult.postValue(Resource.Success(newUser))
                            return@launch
                        }
                        is Resource.Error -> {
                            android.util.Log.e("UserRepository", "❌ Error del backend: ${response.message}")

                            // Verificar si es error de duplicado
                            if (response.message?.contains("email", ignoreCase = true) == true) {
                                _registerResult.postValue(Resource.Error("El email ya está registrado"))
                            } else if (response.message?.contains("usuario", ignoreCase = true) == true) {
                                _registerResult.postValue(Resource.Error("El nombre de usuario ya está en uso"))
                            } else {
                                _registerResult.postValue(Resource.Error(response.message ?: "Error al registrar"))
                            }
                            return@launch
                        }
                        else -> {}
                    }
                } catch (backendEx: Exception) {
                    android.util.Log.e("UserRepository", "❌ Error al registrar en backend: ${backendEx.message}", backendEx)
                }

                // Fallback: verificar en BD local
                val existingEmail = userDao.getUserByEmail(email)
                if (existingEmail != null) {
                    android.util.Log.w("UserRepository", "⚠️ Email ya registrado en local")
                    _registerResult.postValue(Resource.Error("El email ya está registrado"))
                    return@launch
                }

                val existingUsername = userDao.getUserByUsername(username)
                if (existingUsername != null) {
                    android.util.Log.w("UserRepository", "⚠️ Username ya en uso en local")
                    _registerResult.postValue(Resource.Error("El nombre de usuario ya está en uso"))
                    return@launch
                }

                // Crear usuario localmente como último recurso
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    username = username,
                    password = password,
                    name = name,
                    rut = rut,
                    createdAt = System.currentTimeMillis()
                )
                
                android.util.Log.d("UserRepository", "Guardando usuario localmente (backend no disponible)...")
                userDao.insertUser(newUser)
                android.util.Log.w("UserRepository", "⚠️ Usuario guardado solo localmente - Se sincronizará cuando el backend esté disponible")

                _registerResult.postValue(Resource.Success(newUser))
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "❌ ERROR GENERAL en registro: ${e.message}", e)
                _registerResult.postValue(Resource.Error("Error al registrar usuario: ${e.message}"))
            }
        }
    }

    // READ
    fun getUserById(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _userResult.postValue(Resource.Loading())
            try {
                // TODO: Implementar obtención desde backend Spring Boot
                android.util.Log.d("UserRepository", "Obteniendo usuario desde backend...")

                // Por ahora, solo buscar en BD local
                val user = userDao.getUserById(userId)
                if (user != null) {
                    _userResult.postValue(Resource.Success(user))
                } else {
                    _userResult.postValue(Resource.Error("Usuario no encontrado"))
                }
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Error al obtener usuario", e)
                _userResult.postValue(Resource.Error("Error al obtener usuario: ${e.message}"))
            }
        }
    }

    fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _usersResult.postValue(Resource.Loading())
            try {
                // TODO: Implementar obtención desde backend Spring Boot
                android.util.Log.d("UserRepository", "Obteniendo usuarios desde backend...")

                // Por ahora, solo buscar en BD local
                val users = userDao.getAllUsers()
                _usersResult.postValue(Resource.Success(users))
            } catch (e: Exception) {
                _usersResult.postValue(Resource.Error("Error al obtener usuarios: ${e.message}"))
            }
        }
    }

    // UPDATE
    fun updateUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            _userResult.postValue(Resource.Loading())
            try {
                // TODO: Implementar actualización en backend Spring Boot
                android.util.Log.d("UserRepository", "Actualizando usuario en backend...")

                // Actualizar en BD local
                userDao.updateUser(user)
                _userResult.postValue(Resource.Success(user))
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Error al actualizar usuario", e)
                _userResult.postValue(Resource.Error("Error al actualizar usuario: ${e.message}"))
            }
        }
    }

    // DELETE
    fun deleteUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                // TODO: Implementar eliminación en backend Spring Boot
                android.util.Log.d("UserRepository", "Eliminando usuario del backend...")

                // Eliminar de BD local
                userDao.deleteUser(user)
                _deleteResult.postValue(Resource.Success("Usuario eliminado exitosamente"))
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Error al eliminar usuario", e)
                _deleteResult.postValue(Resource.Error("Error al eliminar usuario: ${e.message}"))
            }
        }
    }

    fun deleteUserById(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                // TODO: Implementar eliminación en backend Spring Boot
                android.util.Log.d("UserRepository", "Eliminando usuario del backend...")

                // Eliminar de BD local
                userDao.deleteUserById(userId)
                _deleteResult.postValue(Resource.Success("Usuario eliminado exitosamente"))
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Error al eliminar usuario", e)
                _deleteResult.postValue(Resource.Error("Error al eliminar usuario: ${e.message}"))
            }
        }
    }
}

