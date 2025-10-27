package com.grupo8.fullsound.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.UserDao
import com.grupo8.fullsound.data.models.User
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class UserRepository(private val userDao: UserDao) {

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

    fun register(email: String, username: String, password: String, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _registerResult.postValue(Resource.Loading())
            try {
                // Verificar si el email ya existe
                val existingEmail = userDao.getUserByEmail(email)
                if (existingEmail != null) {
                    _registerResult.postValue(Resource.Error("El email ya está registrado"))
                    return@launch
                }

                // Verificar si el username ya existe
                val existingUsername = userDao.getUserByUsername(username)
                if (existingUsername != null) {
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
                    createdAt = System.currentTimeMillis()
                )
                userDao.insertUser(newUser)
                _registerResult.postValue(Resource.Success(newUser))
            } catch (e: Exception) {
                _registerResult.postValue(Resource.Error("Error al registrar usuario: ${e.message}"))
            }
        }
    }

    // READ
    fun getUserById(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _userResult.postValue(Resource.Loading())
            try {
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
                userDao.deleteUserById(userId)
                _deleteResult.postValue(Resource.Success("Usuario eliminado exitosamente"))
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error("Error al eliminar usuario: ${e.message}"))
            }
        }
    }
}