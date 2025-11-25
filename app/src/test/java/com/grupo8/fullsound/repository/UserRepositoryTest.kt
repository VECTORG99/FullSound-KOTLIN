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

    // TEST 4: Login exitoso - verificar lógica del DAO
    "TEST 4 - login con credenciales válidas debería llamar al DAO correctamente" {
        runTest {
            val user = User("1", "test@test.com", "user", "pass", "Test", "user", null, 0L)
            coEvery { userDao.getUserByEmailOrUsername("test@test.com", "pass") } returns user

            repository.login("test@test.com", "pass")

            // Avanzar coroutines
            advanceUntilIdle()

            // Verificar que se llamó al DAO con los parámetros correctos
            coVerify(exactly = 1) { userDao.getUserByEmailOrUsername("test@test.com", "pass") }
        }
    }

    // TEST 5: Login fallido - verificar lógica del DAO
    "TEST 5 - login con credenciales inválidas debería llamar al DAO correctamente" {
        runTest {
            coEvery { userDao.getUserByEmailOrUsername(any(), any()) } returns null

            repository.login("wrong@test.com", "wrong")

            advanceUntilIdle()

            // Verificar que se llamó al DAO
            coVerify(exactly = 1) { userDao.getUserByEmailOrUsername("wrong@test.com", "wrong") }
        }
    }

    // TEST 6: Register con email duplicado - verificar lógica del DAO
    "TEST 6 - register con email duplicado debería verificar email existente" {
        runTest {
            val existing = User("1", "test@test.com", "user", "pass", "Test", "user", null, 0L)
            coEvery { userDao.getUserByEmail("test@test.com") } returns existing

            repository.register("test@test.com", "newuser", "pass123", "New")

            advanceUntilIdle()

            // Verificar que se llamó a verificar email
            coVerify(exactly = 1) { userDao.getUserByEmail("test@test.com") }
            // No debería intentar insertar porque el email ya existe
            coVerify(exactly = 0) { userDao.insertUser(any()) }
        }
    }
})

