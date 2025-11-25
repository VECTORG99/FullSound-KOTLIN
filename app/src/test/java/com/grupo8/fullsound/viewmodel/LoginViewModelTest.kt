package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.Observer
import com.grupo8.fullsound.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

/**
 * TOP 2 TESTS CRÍTICOS - LoginViewModel
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

    // TEST 9: Validación exitosa
    "TEST 9 - validateForm con datos válidos debería marcar isDataValid=true" {
        val observer = mockk<Observer<LoginFormState>>(relaxed = true)
        viewModel.loginFormState.observeForever(observer)

        viewModel.validateForm("test@test.com", "password123")

        val slot = slot<LoginFormState>()
        verify { observer.onChanged(capture(slot)) }

        slot.captured.emailError shouldBe null
        slot.captured.passwordError shouldBe null
        slot.captured.isDataValid shouldBe true

        viewModel.loginFormState.removeObserver(observer)
    }

    // TEST 10: Validación password corta
    "TEST 10 - validateForm con password corta debería marcar error" {
        val observer = mockk<Observer<LoginFormState>>(relaxed = true)
        viewModel.loginFormState.observeForever(observer)

        viewModel.validateForm("test@test.com", "1234")

        val slot = slot<LoginFormState>()
        verify { observer.onChanged(capture(slot)) }

        slot.captured.passwordError shouldBe "La contraseña debe tener al menos 5 caracteres"
        slot.captured.isDataValid shouldBe false

        viewModel.loginFormState.removeObserver(observer)
    }
})

