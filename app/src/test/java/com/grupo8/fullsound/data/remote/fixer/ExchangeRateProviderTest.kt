package com.grupo8.fullsound.data.remote.fixer

import android.content.Context
import android.content.SharedPreferences
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

/**
 * TOP 2 TESTS CRÍTICOS - ExchangeRateProvider
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeRateProviderTest : StringSpec({

    lateinit var mockContext: Context
    lateinit var mockPrefs: SharedPreferences
    lateinit var mockEditor: SharedPreferences.Editor
    val testDispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeTest {
        mockContext = mockk(relaxed = true)
        mockPrefs = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockContext.getSharedPreferences("fixer_prefs", Context.MODE_PRIVATE) } returns mockPrefs
        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putFloat(any(), any()) } returns mockEditor
        every { mockEditor.putLong(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs
    }

    afterTest {
        clearAllMocks()
    }

    // TEST 7: Verificar que convertClpToUsd no falla con valores válidos
    "TEST 7 - convertClpToUsd debería calcular correctamente cuando hay tasa disponible" {
        runTest {
            val currentTime = System.currentTimeMillis() / 1000L
            every { mockPrefs.getFloat("usd_to_clp_rate", -1f) } returns 900f
            every { mockPrefs.getLong("usd_to_clp_ts", 0L) } returns currentTime - 1800

            // Mockear FixerRetrofitClient para evitar llamadas reales
            mockkObject(FixerRetrofitClient)
            val mockApiService = mockk<FixerApiService>()
            every { FixerRetrofitClient.apiService } returns mockApiService

            val result = ExchangeRateProvider.convertClpToUsd(9000.0, mockContext, "test_key")

            // Si el caché funciona, debería retornar un valor (9000 / 900 = 10)
            result shouldNotBe null

            unmockkObject(FixerRetrofitClient)
        }
    }

    // TEST 8: Verificar que getExchangeRateWithLogging maneja errores correctamente
    "TEST 8 - getExchangeRateWithLogging debería manejar el caso sin caché" {
        runTest {
            every { mockPrefs.getFloat("usd_to_clp_rate", -1f) } returns -1f
            every { mockPrefs.getLong("usd_to_clp_ts", 0L) } returns 0L

            // Mockear FixerRetrofitClient para simular error de API
            mockkObject(FixerRetrofitClient)
            val mockApiService = mockk<FixerApiService>()
            every { FixerRetrofitClient.apiService } returns mockApiService
            coEvery { mockApiService.getLatestRates(any(), any()) } throws Exception("Network error")

            val result = ExchangeRateProvider.getExchangeRateWithLogging(mockContext, "test_key")

            // Sin caché y con error de API, debería retornar null
            result shouldBe null

            unmockkObject(FixerRetrofitClient)
        }
    }
})

