package com.grupo8.fullsound.repository

import androidx.lifecycle.Observer
import com.grupo8.fullsound.data.local.UserDao
import com.grupo8.fullsound.model.User
import com.grupo8.fullsound.utils.Resource
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

/**
 * TOP 3 TESTS CRÍTICOS - UserRepository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest : StringSpec({

    lateinit var userDao: UserDao
    lateinit var repository: UserRepository
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeTest {
        userDao = mockk()
        repository = UserRepository(userDao)
    }

    afterTest {
        clearAllMocks()
    }

    // TEST 4: Login exitoso
    "TEST 4 - login con credenciales válidas debería emitir Success" {
        runTest {
            val user = User("1", "test@test.com", "user", "pass", "Test", "user", null, 0L)
            coEvery { userDao.getUserByEmailOrUsername("test@test.com", "pass") } returns user

            val observer = mockk<Observer<Resource<User>>>(relaxed = true)
            repository.loginResult.observeForever(observer)

            repository.login("test@test.com", "pass")
            advanceUntilIdle()

            verify { observer.onChanged(match { it is Resource.Success && it.data == user }) }
            repository.loginResult.removeObserver(observer)
        }
    }

    // TEST 5: Login fallido
    "TEST 5 - login con credenciales inválidas debería emitir Error" {
        runTest {
            coEvery { userDao.getUserByEmailOrUsername(any(), any()) } returns null

            val observer = mockk<Observer<Resource<User>>>(relaxed = true)
            repository.loginResult.observeForever(observer)

            repository.login("wrong@test.com", "wrong")
            advanceUntilIdle()

            verify { observer.onChanged(match {
                it is Resource.Error && it.message == "Credenciales inválidas"
            }) }
            repository.loginResult.removeObserver(observer)
        }
    }

    // TEST 6: Register con email duplicado
    "TEST 6 - register con email duplicado debería emitir Error" {
        runTest {
            val existing = User("1", "test@test.com", "user", "pass", "Test", "user", null, 0L)
            coEvery { userDao.getUserByEmail("test@test.com") } returns existing

            val observer = mockk<Observer<Resource<User>>>(relaxed = true)
            repository.registerResult.observeForever(observer)

            repository.register("test@test.com", "newuser", "pass123", "New")
            advanceUntilIdle()

            verify { observer.onChanged(match {
                it is Resource.Error && it.message == "El email ya está registrado"
            }) }
            repository.registerResult.removeObserver(observer)
        }
    }
})

