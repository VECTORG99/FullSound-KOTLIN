package com.grupo8.fullsound.data.remote.fixer

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExchangeRateProvider {
    private const val PREFS = "fixer_prefs"
    private const val KEY_RATE = "usd_to_clp_rate"
    private const val KEY_TS = "usd_to_clp_ts"

    /**
     * Convierte un monto de CLP a USD usando la tasa de cambio
     * @param clpAmount Monto en pesos chilenos
     * @param context Contexto de la aplicación
     * @param apiKey API key de Fixer
     * @return Monto en USD o null si hay error
     */
    suspend fun convertClpToUsd(clpAmount: Double, context: Context, apiKey: String): Double? {
        val usdToClpRate = getUsdToClpRate(context, apiKey) ?: return null
        // Si 1 USD = X CLP, entonces CLP / X = USD
        return clpAmount / usdToClpRate
    }

    /**
     * Obtiene la tasa de cambio con caché y logging
     */
    suspend fun getExchangeRateWithLogging(context: Context, apiKey: String): Double? {
        val rate = getUsdToClpRate(context, apiKey)
        if (rate != null) {
            android.util.Log.d("ExchangeRateProvider", "💱 Tasa de cambio: 1 USD = $rate CLP")
        } else {
            android.util.Log.w("ExchangeRateProvider", "⚠️ No se pudo obtener la tasa de cambio")
        }
        return rate
    }

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
