// ...new file...
package com.grupo8.fullsound.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

object FormatUtils {
    /**
     * Formatea un monto (Double) a formato CLP:
     * - Redondea al peso entero más cercano
     * - Agrupa miles con '.'
     * - No muestra decimales
     * Resultado: "$ 12.345"
     */
    fun formatClp(amount: Double): String {
        val nf: NumberFormat = NumberFormat.getIntegerInstance(Locale("es", "CL"))
        nf.isGroupingUsed = true
        val rounded = round(amount).toLong()
        return "$ ${nf.format(rounded)}"
    }
}

