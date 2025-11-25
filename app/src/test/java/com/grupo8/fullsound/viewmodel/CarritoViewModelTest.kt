package com.grupo8.fullsound.viewmodel

import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.repository.CarritoRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

/**
 * TOP 2 TESTS CRÍTICOS - CarritoViewModel
 * Nota: Tests sin LiveData observers, verifican que se llama al repository correctamente
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest : StringSpec({

    lateinit var repository: CarritoRepository
    lateinit var viewModel: CarritoViewModel
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeTest {
        repository = mockk()
        every { repository.carritoItems } returns flowOf(emptyList())
        viewModel = CarritoViewModel(repository)
    }

    afterTest {
        clearAllMocks()
    }

    // TEST 11: Agregar beat exitosamente - verificar llamada al repository
    "TEST 11 - addBeatToCarrito debería llamar al repository cuando se agrega exitosamente" {
        runTest {
            val beat = Beat(1, "Beat", "Artist", 120, "img1", "mp3", 10.0)
            coEvery { repository.addBeatToCarrito(beat) } returns true

            viewModel.addBeatToCarrito(beat)

            // Avanzar dispatcher
            advanceUntilIdle()

            // Verificar que se llamó al repository
            coVerify(exactly = 1) { repository.addBeatToCarrito(beat) }
        }
    }

    // TEST 12: Beat duplicado - verificar llamada al repository
    "TEST 12 - addBeatToCarrito debería llamar al repository cuando beat ya existe" {
        runTest {
            val beat = Beat(1, "Beat", "Artist", 120, "img1", "mp3", 10.0)
            coEvery { repository.addBeatToCarrito(beat) } returns false

            viewModel.addBeatToCarrito(beat)

            // Avanzar dispatcher
            advanceUntilIdle()

            // Verificar que se llamó al repository
            coVerify(exactly = 1) { repository.addBeatToCarrito(beat) }
        }
    }
})

