package com.grupo8.fullsound.utils
// ...existing code...
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round
// ...existing code...
object FormatUtils {
    fun formatClp(amount: Double): String {
        val nf: NumberFormat = NumberFormat.getIntegerInstance(Locale("es", "CL"))
        nf.isGroupingUsed = true
        val rounded = round(amount).toLong()
        return "$ ${nf.format(rounded)}"
    }
}
// ...existing code...

