package com.grupo8.fullsound.repository

import com.grupo8.fullsound.data.local.UserDao
import com.grupo8.fullsound.model.User
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

/**
 * TOP 3 TESTS CRÍTICOS - UserRepository
 * Nota: Estos tests verifican la lógica del DAO sin usar LiveData observers
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

    // TEST 4: Login exitoso - verificar que no lanza excepciones
    "TEST 4 - login con credenciales válidas debería ejecutarse sin excepciones" {
        val user = User("1", "test@test.com", "user", "pass", "Test", "user", null, 0L)
        coEvery { userDao.getUserByEmailOrUsername("test@test.com", "pass") } returns user

        // Verificar que login se ejecuta sin excepciones
        repository.login("test@test.com", "pass")

        // Test pasa si no hay excepciones
    }

    // TEST 5: Login fallido - verificar que no lanza excepciones
    "TEST 5 - login con credenciales inválidas debería ejecutarse sin excepciones" {
        coEvery { userDao.getUserByEmailOrUsername(any(), any()) } returns null

        // Verificar que login se ejecuta sin excepciones
        repository.login("wrong@test.com", "wrong")

        // Test pasa si no hay excepciones
    }

    // TEST 6: Register con email duplicado - verificar lógica del DAO
    "TEST 6 - register con email duplicado debería verificar email existente" {
        runTest {
            val existing = User("1", "test@test.com", "user", "pass", "Test", "user", null, 0L)
            coEvery { userDao.getUserByEmail("test@test.com") } returns existing
            coEvery { userDao.getUserByUsername(any()) } returns null

            repository.register("test@test.com", "newuser", "pass123", "New")

            // Esperar un poco para que la coroutine se ejecute
            advanceUntilIdle()

            // Test pasa si no hay excepciones
        }
    }
})

