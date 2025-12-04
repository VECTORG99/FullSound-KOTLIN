package com.grupo8.fullsound.utils

import org.junit.Assert.*
import org.junit.Test

class ConstantsTest {

    @Test
    fun `calcularIva con monto de 1000 retorna 190`() {
        val subtotal = 1000.0
        val ivaEsperado = 190.0
        val ivaCalculado = Constants.calcularIva(subtotal)

        assertEquals(ivaEsperado, ivaCalculado, 0.01)
    }

    @Test
    fun `calcularIva con monto de 10000 retorna 1900`() {
        val subtotal = 10000.0
        val ivaEsperado = 1900.0
        val ivaCalculado = Constants.calcularIva(subtotal)

        assertEquals(ivaEsperado, ivaCalculado, 0.01)
    }

    @Test
    fun `calcularIva con monto de 0 retorna 0`() {
        val subtotal = 0.0
        val ivaEsperado = 0.0
        val ivaCalculado = Constants.calcularIva(subtotal)

        assertEquals(ivaEsperado, ivaCalculado, 0.01)
    }

    @Test
    fun `calcularTotalConIva con monto de 1000 retorna 1190`() {
        val subtotal = 1000.0
        val totalEsperado = 1190.0
        val totalCalculado = Constants.calcularTotalConIva(subtotal)

        assertEquals(totalEsperado, totalCalculado, 0.01)
    }

    @Test
    fun `calcularTotalConIva con monto de 10000 retorna 11900`() {
        val subtotal = 10000.0
        val totalEsperado = 11900.0
        val totalCalculado = Constants.calcularTotalConIva(subtotal)

        assertEquals(totalEsperado, totalCalculado, 0.01)
    }

    @Test
    fun `calcularTotalConIva con monto de 5250 retorna 6247punto5`() {
        val subtotal = 5250.0
        val totalEsperado = 6247.5
        val totalCalculado = Constants.calcularTotalConIva(subtotal)

        assertEquals(totalEsperado, totalCalculado, 0.01)
    }

    @Test
    fun `IVA_PERCENTAGE es 0punto19`() {
        assertEquals(0.19, Constants.IVA_PERCENTAGE, 0.001)
    }

    @Test
    fun `calcularIva con monto negativo retorna valor negativo`() {
        val subtotal = -1000.0
        val ivaEsperado = -190.0
        val ivaCalculado = Constants.calcularIva(subtotal)

        assertEquals(ivaEsperado, ivaCalculado, 0.01)
    }

    @Test
    fun `calcularTotalConIva con valores decimales funciona correctamente`() {
        val subtotal = 999.99
        val ivaEsperado = 189.9981
        val totalEsperado = 1189.9881

        val ivaCalculado = Constants.calcularIva(subtotal)
        val totalCalculado = Constants.calcularTotalConIva(subtotal)

        assertEquals(ivaEsperado, ivaCalculado, 0.01)
        assertEquals(totalEsperado, totalCalculado, 0.01)
    }

    @Test
    fun `verificar que IVA del 19 por ciento es correcto para ejemplo real`() {
        // Ejemplo: Carrito con 3 beats de $9.99 cada uno
        val subtotal = 9.99 * 3 // $29.97
        val ivaCalculado = Constants.calcularIva(subtotal)
        val totalCalculado = Constants.calcularTotalConIva(subtotal)

        // IVA esperado: $29.97 * 0.19 = $5.6943
        // Total esperado: $29.97 + $5.6943 = $35.6643
        assertEquals(5.6943, ivaCalculado, 0.01)
        assertEquals(35.6643, totalCalculado, 0.01)
    }
}

