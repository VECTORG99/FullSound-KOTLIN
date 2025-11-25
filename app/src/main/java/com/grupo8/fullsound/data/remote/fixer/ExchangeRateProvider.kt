package com.grupo8.fullsound.data.remote.fixer

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExchangeRateProvider {
    private const val PREFS = "fixer_prefs"
    private const val KEY_RATE = "usd_to_clp_rate"
    private const val KEY_TS = "usd_to_clp_ts"

    suspend fun getUsdToClpRate(context: Context, apiKey: String): Double? {
        return withContext(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val cachedRate = prefs.getFloat(KEY_RATE, -1f).toDouble()
                val ts = prefs.getLong(KEY_TS, 0L)
                val now = System.currentTimeMillis() / 1000L
                // Usar cache si es reciente (1 hora)
                if (cachedRate > 0 && (now - ts) < 3600) {
                    return@withContext cachedRate
                }
                val resp = FixerRetrofitClient.apiService.getLatestRates(apiKey, "USD,CLP")
                val rates = resp.rates
                if (rates != null) {
                    val usd = rates["USD"]
                    val clp = rates["CLP"]
                    if (usd != null && clp != null) {
                        val rate = clp / usd
                        // Guardar en cache
                        prefs.edit().putFloat(KEY_RATE, rate.toFloat()).putLong(KEY_TS, now).apply()
                        return@withContext rate
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // fallback to cached value even if expired
            try {
                val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val cachedRate = prefs.getFloat(KEY_RATE, -1f).toDouble()
                if (cachedRate > 0) return@withContext cachedRate
            } catch (_: Exception) {}
            null
        }
    }
}
