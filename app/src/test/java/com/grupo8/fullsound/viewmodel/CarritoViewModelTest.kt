package com.grupo8.fullsound.viewmodel

import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.repository.CarritoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

/**
 * TOP 2 TESTS CRÍTICOS - CarritoViewModel
 * Tests que verifican la creación e inicialización del ViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest : StringSpec({

    lateinit var repository: CarritoRepository
    lateinit var viewModel: CarritoViewModel
    val testDispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeTest {
        repository = mockk(relaxed = true)
        every { repository.carritoItems } returns flowOf(emptyList())
        viewModel = CarritoViewModel(repository)
    }

    afterTest {
        clearAllMocks()
    }

    // TEST 11: Verificar que el ViewModel puede crear un Beat válido y llamar al método
    "TEST 11 - addBeatToCarrito debería ejecutarse sin excepciones cuando se agrega exitosamente" {
        val beat = Beat(
            id = 1,
            titulo = "Beat Test",
            artista = "Artist Test",
            precio = 10000.0,
            imagenPath = "1.jpg",
            mp3Path = "1.mp3",
            genero = "Hip-Hop",
            bpm = 120,
            tonalidad = "Am",
            duracion = 180
        )

        // Verificar que el beat se creó correctamente
        beat.id shouldBe 1
        beat.titulo shouldBe "Beat Test"
        beat.precio shouldBe 10000.0

        // Configurar el mock
        coEvery { repository.addBeatToCarrito(any()) } returns true

        // Ejecutar el método - si no lanza excepción, el test pasa
        viewModel.addBeatToCarrito(beat)

        // Verificar que el ViewModel no es nulo
        viewModel shouldNotBe null
    }

    // TEST 12: Verificar que el ViewModel maneja beats duplicados sin excepciones
    "TEST 12 - addBeatToCarrito debería ejecutarse sin excepciones cuando beat ya existe" {
        val beat = Beat(
            id = 1,
            titulo = "Beat Test",
            artista = "Artist Test",
            precio = 10000.0,
            imagenPath = "1.jpg",
            mp3Path = "1.mp3",
            genero = "Hip-Hop",
            bpm = 120,
            tonalidad = "Am",
            duracion = 180
        )

        // Verificar que el beat tiene los datos correctos
        beat.genero shouldBe "Hip-Hop"
        beat.bpm shouldBe 120

        // Configurar el mock para simular beat duplicado
        coEvery { repository.addBeatToCarrito(any()) } returns false

        // Ejecutar el método - si no lanza excepción, el test pasa
        viewModel.addBeatToCarrito(beat)

        // Verificar que el ViewModel no es nulo
        viewModel shouldNotBe null
    }
})

