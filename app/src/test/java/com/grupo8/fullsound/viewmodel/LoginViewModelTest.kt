package com.grupo8.fullsound.viewmodel

import com.grupo8.fullsound.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

/**
 * TOP 2 TESTS CRÍTICOS - LoginViewModel
 * Nota: Tests verifican delegación al repository sin depender de Android framework
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    lateinit var userRepository: UserRepository
    lateinit var viewModel: LoginViewModel
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeTest {
        userRepository = mockk(relaxed = true)
        viewModel = LoginViewModel(userRepository)
    }

    afterTest {
        clearAllMocks()
    }

    // TEST 9: Login delega correctamente al repository
    "TEST 9 - login debería delegar al repository correctamente" {
        // Llamar a login
        viewModel.login("test@test.com", "password123")

        // Verificar que se llamó al repository con los parámetros correctos
        verify(exactly = 1) { userRepository.login("test@test.com", "password123") }
    }

    // TEST 10: Login con diferentes credenciales delega correctamente
    "TEST 10 - login debería delegar cualquier credencial al repository" {
        // Llamar a login con diferentes credenciales
        viewModel.login("user", "pass")

        // Verificar que se llamó al repository
        verify(exactly = 1) { userRepository.login("user", "pass") }
    }
})

