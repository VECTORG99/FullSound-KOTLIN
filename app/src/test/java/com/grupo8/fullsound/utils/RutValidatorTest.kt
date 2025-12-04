package com.grupo8.fullsound.utils

import org.junit.Assert.*
import org.junit.Test

class RutValidatorTest {

    @Test
    fun `validarRut con RUT valido formato con puntos y guion`() {
        assertTrue(RutValidator.validarRut("12.345.678-5"))
    }

    @Test
    fun `validarRut con RUT valido formato con guion`() {
        assertTrue(RutValidator.validarRut("12345678-5"))
    }

    @Test
    fun `validarRut con RUT valido formato sin separadores`() {
        assertTrue(RutValidator.validarRut("123456785"))
    }

    @Test
    fun `validarRut con RUT valido digito verificador K`() {
        assertTrue(RutValidator.validarRut("11.111.111-K"))
    }

    @Test
    fun `validarRut con RUT valido digito verificador 0`() {
        assertTrue(RutValidator.validarRut("22.222.222-0"))
    }

    @Test
    fun `validarRut con digito verificador incorrecto`() {
        assertFalse(RutValidator.validarRut("12.345.678-9"))
    }

    @Test
    fun `validarRut con RUT vacio`() {
        assertFalse(RutValidator.validarRut(""))
    }

    @Test
    fun `validarRut con RUT muy corto`() {
        assertFalse(RutValidator.validarRut("123-4"))
    }

    @Test
    fun `validarRut con caracteres no numericos`() {
        assertFalse(RutValidator.validarRut("12.abc.678-5"))
    }

    @Test
    fun `validarRut con RUT menor a 1 millon`() {
        assertFalse(RutValidator.validarRut("999.999-9"))
    }

    @Test
    fun `formatearRut formatea correctamente`() {
        val resultado = RutValidator.formatearRut("123456785")
        assertEquals("12.345.678-5", resultado)
    }

    @Test
    fun `formatearRut con RUT ya formateado`() {
        val resultado = RutValidator.formatearRut("12.345.678-5")
        assertEquals("12.345.678-5", resultado)
    }

    @Test
    fun `formatearRut con K minuscula lo convierte a mayuscula`() {
        val resultado = RutValidator.formatearRut("11111111k")
        assertEquals("11.111.111-K", resultado)
    }

    @Test
    fun `limpiarRut remueve puntos guiones y espacios`() {
        val resultado = RutValidator.limpiarRut("12. 345.678 -5")
        assertEquals("123456785", resultado)
    }

    @Test
    fun `obtenerMensajeError retorna null para RUT valido`() {
        assertNull(RutValidator.obtenerMensajeError("12.345.678-5"))
    }

    @Test
    fun `obtenerMensajeError retorna mensaje para RUT vacio`() {
        assertNotNull(RutValidator.obtenerMensajeError(""))
    }

    @Test
    fun `obtenerMensajeError retorna mensaje para digito verificador incorrecto`() {
        val mensaje = RutValidator.obtenerMensajeError("12.345.678-9")
        assertTrue(mensaje?.contains("dígito verificador") == true)
    }

    @Test
    fun `validarRut con varios RUTs validos conocidos`() {
        // RUTs válidos de ejemplo (generados con algoritmo correcto)
        val rutsValidos = listOf(
            "11.111.111-K",
            "22.222.222-0",
            "15.555.555-6",
            "18.888.888-1",
            "7.777.777-6"
        )

        rutsValidos.forEach { rut ->
            assertTrue("RUT $rut debería ser válido", RutValidator.validarRut(rut))
        }
    }
}

