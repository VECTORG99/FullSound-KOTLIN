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

    fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _loginResult.postValue(Resource.Loading())
            try {
                val user = userDao.getUser(email, password)
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

    fun register(email: String, password: String, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _registerResult.postValue(Resource.Loading())
            try {
                // Verificar si el usuario ya existe
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    _registerResult.postValue(Resource.Error("El usuario ya existe"))
                    return@launch
                }

                // Crear nuevo usuario
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    email = email,
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
}