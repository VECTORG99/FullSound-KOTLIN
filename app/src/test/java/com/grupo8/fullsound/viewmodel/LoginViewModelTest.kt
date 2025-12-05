package com.grupo8.fullsound.viewmodel

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.grupo8.fullsound.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Función de extensión para obtener el valor de un LiveData de forma síncrona en tests
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T? {
    var data: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { value ->
        data = value
        latch.countDown()
    }

    this.observeForever(observer)

    try {
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }

    return data
}

/**
 * TOP 2 TESTS CRÍTICOS - LoginViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    lateinit var userRepository: UserRepository
    lateinit var viewModel: LoginViewModel
    val testDispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
        // Configurar ArchTaskExecutor para que LiveData funcione síncronamente en tests
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    afterSpec {
        Dispatchers.resetMain()
        // Limpiar ArchTaskExecutor
        ArchTaskExecutor.getInstance().setDelegate(null)
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
        // Ejecutar la validación
        viewModel.validateForm("test@test.com", "password123")

        // Obtener el valor del LiveData
        val formState = viewModel.loginFormState.getOrAwaitValue()

        // Verificar el estado
        formState shouldNotBe null
        formState!!.emailError shouldBe null
        formState.passwordError shouldBe null
        formState.isDataValid shouldBe true
    }

    // TEST 10: Validación password corta
    "TEST 10 - validateForm con password corta debería marcar error" {
        // Ejecutar la validación con password inválida
        viewModel.validateForm("test@test.com", "1234")

        // Obtener el valor del LiveData
        val formState = viewModel.loginFormState.getOrAwaitValue()

        // Verificar que hay error de password y el formulario no es válido
        formState shouldNotBe null
        formState!!.passwordError shouldNotBe null
        formState.passwordError shouldBe "La contraseña debe tener al menos 5 caracteres"
        formState.isDataValid shouldBe false
    }
})

