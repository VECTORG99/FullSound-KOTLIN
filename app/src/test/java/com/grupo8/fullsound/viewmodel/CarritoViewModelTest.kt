package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.Observer
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

    // TEST 11: Agregar beat exitosamente
    "TEST 11 - addBeatToCarrito debería emitir Success cuando repo retorna true" {
        runTest {
            val beat = Beat(1, "Beat", "Artist", 120, "img1", "mp3", 10.0)
            coEvery { repository.addBeatToCarrito(beat) } returns true

            val observer = mockk<Observer<AddToCarritoResult>>(relaxed = true)
            viewModel.addToCarritoResult.observeForever(observer)

            viewModel.addBeatToCarrito(beat)
            advanceUntilIdle()

            verify { observer.onChanged(match {
                it is AddToCarritoResult.Success && it.beatTitle == "Beat"
            }) }

            viewModel.addToCarritoResult.removeObserver(observer)
        }
    }

    // TEST 12: Beat duplicado
    "TEST 12 - addBeatToCarrito debería emitir AlreadyExists cuando repo retorna false" {
        runTest {
            val beat = Beat(1, "Beat", "Artist", 120, "img1", "mp3", 10.0)
            coEvery { repository.addBeatToCarrito(beat) } returns false

            val observer = mockk<Observer<AddToCarritoResult>>(relaxed = true)
            viewModel.addToCarritoResult.observeForever(observer)

            viewModel.addBeatToCarrito(beat)
            advanceUntilIdle()

            verify { observer.onChanged(match {
                it is AddToCarritoResult.AlreadyExists && it.beatTitle == "Beat"
            }) }

            viewModel.addToCarritoResult.removeObserver(observer)
        }
    }
})

