package com.grupo8.fullsound.data.remote.fixer

import android.content.Context
import android.content.SharedPreferences
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
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
    lateinit var mockApiService: FixerApiService
    val testDispatcher = StandardTestDispatcher()

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
        mockApiService = mockk()

        every { mockContext.getSharedPreferences("fixer_prefs", Context.MODE_PRIVATE) } returns mockPrefs
        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putFloat(any(), any()) } returns mockEditor
        every { mockEditor.putLong(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs

        mockkObject(FixerRetrofitClient)
        every { FixerRetrofitClient.apiService } returns mockApiService
    }

    afterTest {
        clearAllMocks()
        unmockkAll()
    }

    // TEST 7: Usar cache válido
    "TEST 7 - getUsdToClpRate debería usar cache cuando es válido y reciente" {
        runTest {
            val currentTime = System.currentTimeMillis() / 1000L
            every { mockPrefs.getFloat("usd_to_clp_rate", -1f) } returns 900f
            every { mockPrefs.getLong("usd_to_clp_ts", 0L) } returns currentTime - 1800

            val rate = ExchangeRateProvider.getUsdToClpRate(mockContext, "test_key")

            rate shouldBe 900.0
            coVerify(exactly = 0) { mockApiService.getLatestRates(any(), any()) }
        }
    }

    // TEST 8: Llamar API cuando cache expirado
    "TEST 8 - getUsdToClpRate debería llamar API cuando cache está expirado" {
        runTest {
            val oldTime = (System.currentTimeMillis() / 1000L) - 7200
            every { mockPrefs.getFloat("usd_to_clp_rate", -1f) } returns 900f
            every { mockPrefs.getLong("usd_to_clp_ts", 0L) } returns oldTime

            val response = FixerLatestResponse(
                success = true,
                timestamp = System.currentTimeMillis() / 1000L,
                base = "EUR",
                date = "2025-11-24",
                rates = mapOf("USD" to 1.0, "CLP" to 950.0)
            )
            coEvery { mockApiService.getLatestRates("test_key", "USD,CLP") } returns response

            val rate = ExchangeRateProvider.getUsdToClpRate(mockContext, "test_key")

            rate shouldBe 950.0
            coVerify(exactly = 1) { mockApiService.getLatestRates("test_key", "USD,CLP") }
            verify { mockEditor.putFloat("usd_to_clp_rate", 950f) }
        }
    }
})

