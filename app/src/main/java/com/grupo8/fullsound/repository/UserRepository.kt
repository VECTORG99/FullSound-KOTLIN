package com.grupo8.fullsound.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.UserDao
import com.grupo8.fullsound.model.User
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import com.grupo8.fullsound.data.remote.supabase.repository.SupabaseUserRepository

class UserRepository(
    private val userDao: UserDao,
    private val supabaseRepo: SupabaseUserRepository = SupabaseUserRepository()
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
                // Intentar login desde Supabase primero
                try {
                    val supabaseUser = supabaseRepo.getUserByEmailOrUsername(emailOrUsername, password)
                    if (supabaseUser != null) {
                        // Guardar/actualizar en caché local
                        userDao.insertUser(supabaseUser)
                        _loginResult.postValue(Resource.Success(supabaseUser))
                        return@launch
                    }
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
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

                // Verificar en Supabase primero si el email o username ya existen
                var emailExists = false
                var usernameExists = false
                
                try {
                    android.util.Log.d("UserRepository", "Verificando si email existe en Supabase...")
                    val supabaseEmailUser = supabaseRepo.getUserByEmail(email)
                    emailExists = supabaseEmailUser != null
                    android.util.Log.d("UserRepository", "Email existe: $emailExists")

                    android.util.Log.d("UserRepository", "Verificando si username existe en Supabase...")
                    val supabaseUsernameUser = supabaseRepo.getUserByUsername(username)
                    usernameExists = supabaseUsernameUser != null
                    android.util.Log.d("UserRepository", "Username existe: $usernameExists")
                } catch (supabaseEx: Exception) {
                    android.util.Log.e("UserRepository", "❌ Error al verificar en Supabase: ${supabaseEx.message}", supabaseEx)
                    // Si falla Supabase, verificar en BD local
                    val existingEmail = userDao.getUserByEmail(email)
                    emailExists = existingEmail != null
                    
                    val existingUsername = userDao.getUserByUsername(username)
                    usernameExists = existingUsername != null
                }
                
                if (emailExists) {
                    android.util.Log.w("UserRepository", "⚠️ Email ya registrado")
                    _registerResult.postValue(Resource.Error("El email ya está registrado"))
                    return@launch
                }
                
                if (usernameExists) {
                    android.util.Log.w("UserRepository", "⚠️ Username ya en uso")
                    _registerResult.postValue(Resource.Error("El nombre de usuario ya está en uso"))
                    return@launch
                }

                // Crear nuevo usuario
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    username = username,
                    password = password,
                    name = name,
                    rut = rut,
                    createdAt = System.currentTimeMillis()
                )
                
                android.util.Log.d("UserRepository", "Usuario creado localmente con ID: ${newUser.id}")

                // Intentar insertar en Supabase
                android.util.Log.d("UserRepository", "🚀 Intentando insertar en Supabase...")
                val supabaseUser = try {
                    val result = supabaseRepo.insertUser(newUser)
                    if (result != null) {
                        android.util.Log.d("UserRepository", "✅ Usuario insertado en Supabase con ID: ${result.id}")
                    } else {
                        android.util.Log.e("UserRepository", "❌ insertUser retornó null")
                    }
                    result
                } catch (supabaseEx: Exception) {
                    android.util.Log.e("UserRepository", "❌ ERROR al insertar en Supabase: ${supabaseEx.message}", supabaseEx)
                    android.util.Log.e("UserRepository", "   Tipo de error: ${supabaseEx.javaClass.simpleName}")
                    supabaseEx.printStackTrace()
                    null
                }
                
                // Insertar en BD local (con el usuario de Supabase si se obtuvo)
                val finalUser = supabaseUser ?: newUser
                android.util.Log.d("UserRepository", "Guardando en Room DB...")
                userDao.insertUser(finalUser)
                android.util.Log.d("UserRepository", "✅ Usuario guardado en Room DB")

                if (supabaseUser != null) {
                    android.util.Log.d("UserRepository", "✅ REGISTRO EXITOSO - Usuario en Supabase Y Room")
                } else {
                    android.util.Log.w("UserRepository", "⚠️ REGISTRO PARCIAL - Usuario solo en Room (Supabase falló)")
                }

                _registerResult.postValue(Resource.Success(finalUser))
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
                // Intentar obtener desde Supabase primero
                try {
                    val supabaseUser = supabaseRepo.getUserById(userId)
                    if (supabaseUser != null) {
                        // Actualizar caché local
                        userDao.insertUser(supabaseUser)
                        _userResult.postValue(Resource.Success(supabaseUser))
                        return@launch
                    }
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
                }

                // Fallback a BD local
                val user = userDao.getUserById(userId)
                if (user != null) {
                    _userResult.postValue(Resource.Success(user))
                } else {
                    _userResult.postValue(Resource.Error("Usuario no encontrado"))
                }
            } catch (e: Exception) {
                _userResult.postValue(Resource.Error("Error al obtener usuario: ${e.message}"))
            }
        }
    }

    fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _usersResult.postValue(Resource.Loading())
            try {
                // Intentar obtener desde Supabase primero
                try {
                    val supabaseUsers = supabaseRepo.getAllUsers()
                    if (supabaseUsers.isNotEmpty()) {
                        // Actualizar caché local
                        supabaseUsers.forEach { userDao.insertUser(it) }
                        _usersResult.postValue(Resource.Success(supabaseUsers))
                        return@launch
                    }
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
                }

                // Fallback a BD local
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
                // Intentar actualizar en Supabase primero
                try {
                    supabaseRepo.updateUser(user)
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
                }

                // Actualizar en BD local
                userDao.updateUser(user)
                _userResult.postValue(Resource.Success(user))
            } catch (e: Exception) {
                _userResult.postValue(Resource.Error("Error al actualizar usuario: ${e.message}"))
            }
        }
    }

    // DELETE
    fun deleteUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                // Intentar eliminar de Supabase primero
                try {
                    supabaseRepo.deleteUser(user.id)
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
                }

                // Eliminar de BD local
                userDao.deleteUser(user)
                _deleteResult.postValue(Resource.Success("Usuario eliminado exitosamente"))
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error("Error al eliminar usuario: ${e.message}"))
            }
        }
    }

    fun deleteUserById(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                // Intentar eliminar de Supabase primero
                try {
                    supabaseRepo.deleteUser(userId)
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
                }

                // Eliminar de BD local
                userDao.deleteUserById(userId)
                _deleteResult.postValue(Resource.Success("Usuario eliminado exitosamente"))
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error("Error al eliminar usuario: ${e.message}"))
            }
        }
    }
}

