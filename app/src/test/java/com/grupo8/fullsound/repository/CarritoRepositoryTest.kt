package com.grupo8.fullsound.repository

import com.grupo8.fullsound.data.local.CarritoDao
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.model.CarritoItem
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

/**
 * TOP 3 TESTS CRÍTICOS - CarritoRepository
 */
class CarritoRepositoryTest : StringSpec({

    lateinit var carritoDao: CarritoDao
    lateinit var repository: CarritoRepository

    beforeTest {
        carritoDao = mockk()
        // Mockear getAllItems() que se llama en el constructor del repository
        every { carritoDao.getAllItems() } returns flowOf(emptyList())
        repository = CarritoRepository(carritoDao)
    }

    afterTest {
        clearAllMocks()
    }

    // TEST 1: Agregar beat nuevo al carrito
    "TEST 1 - addBeatToCarrito debería agregar beat nuevo exitosamente" {
        runBlocking {
            val beat = Beat(1, "Beat Test", "Artist", 120, "img1", "mp3", 10.0)
            coEvery { carritoDao.getItemByBeatId(1) } returns null
            coEvery { carritoDao.insertItem(any()) } just Runs

            val result = repository.addBeatToCarrito(beat)

            result shouldBe true
            coVerify(exactly = 1) { carritoDao.insertItem(any()) }
        }
    }

    // TEST 2: Rechazar beat duplicado
    "TEST 2 - addBeatToCarrito debería rechazar beat duplicado" {
        runBlocking {
            val beat = Beat(1, "Beat Test", "Artist", 120, "img1", "mp3", 10.0)
            val existing = CarritoItem(1, 1, "Beat Test", "Artist", 10.0, "img1", 1)
            coEvery { carritoDao.getItemByBeatId(1) } returns existing

            val result = repository.addBeatToCarrito(beat)

            result shouldBe false
            coVerify(exactly = 0) { carritoDao.insertItem(any()) }
        }
    }

    // TEST 3: Total price con null
    "TEST 3 - getTotalPrice debería retornar 0.0 cuando dao retorna null" {
        runBlocking {
            coEvery { carritoDao.getTotalPrice() } returns null

            val result = repository.getTotalPrice()

            result shouldBe 0.0
        }
    }
})

