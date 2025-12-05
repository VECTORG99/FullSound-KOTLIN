package com.grupo8.fullsound.utils

/**
 * Validador de RUT chileno.
 * Implementa el algoritmo de módulo 11 para validar el dígito verificador.
 */
object RutValidator {

    /**
     * Valida si un RUT chileno es válido.
     * Acepta formatos: 12.345.678-9, 12345678-9, 123456789
     *
     * @param rut El RUT a validar
     * @return true si el RUT es válido, false en caso contrario
     */
    fun validarRut(rut: String): Boolean {
        // Remover puntos, guiones y espacios
        val rutLimpio = limpiarRut(rut)

        if (rutLimpio.length < 2) return false

        // Separar número y dígito verificador
        val rutNumero = rutLimpio.substring(0, rutLimpio.length - 1)
        val digitoVerificador = rutLimpio.last().uppercaseChar()

        // Validar que el número sea numérico
        if (!rutNumero.all { it.isDigit() }) return false

        // Validar que el número no sea demasiado corto o largo
        val numero = rutNumero.toIntOrNull() ?: return false
        if (numero < 1000000 || numero > 99999999) return false

        // Calcular dígito verificador esperado
        val dvCalculado = calcularDigitoVerificador(numero)

        return dvCalculado == digitoVerificador
    }

    /**
     * Calcula el dígito verificador de un RUT usando el algoritmo de módulo 11.
     *
     * @param rut El número del RUT sin dígito verificador
     * @return El dígito verificador calculado ('0'-'9' o 'K')
     */
    private fun calcularDigitoVerificador(rut: Int): Char {
        var suma = 0
        var multiplicador = 2
        var rutTemp = rut

        // Recorrer cada dígito del RUT de derecha a izquierda
        while (rutTemp > 0) {
            suma += (rutTemp % 10) * multiplicador
            rutTemp /= 10
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = suma % 11
        val dv = 11 - resto

        return when (dv) {
            11 -> '0'
            10 -> 'K'
            else -> dv.toString().first()
        }
    }

    /**
     * Limpia el RUT removiendo puntos, guiones y espacios.
     *
     * @param rut El RUT a limpiar
     * @return El RUT limpio
     */
    fun limpiarRut(rut: String): String {
        return rut.replace(".", "")
            .replace("-", "")
            .replace(" ", "")
            .trim()
    }

    /**
     * Formatea un RUT en el formato estándar: 12.345.678-9
     *
     * @param rut El RUT a formatear
     * @return El RUT formateado o el string original si no es válido
     */
    fun formatearRut(rut: String): String {
        val rutLimpio = limpiarRut(rut)
        if (rutLimpio.length < 2) return rut

        val numero = rutLimpio.substring(0, rutLimpio.length - 1)
        val dv = rutLimpio.last().uppercaseChar()

        // Formatear el número con puntos de miles
        val numeroFormateado = numero.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()

        return "$numeroFormateado-$dv"
    }

    /**
     * Obtiene un mensaje de error descriptivo para un RUT inválido.
     *
     * @param rut El RUT a validar
     * @return Un mensaje de error específico o null si el RUT es válido
     */
    fun obtenerMensajeError(rut: String): String? {
        if (rut.isBlank()) {
            return "El RUT no puede estar vacío"
        }

        val rutLimpio = limpiarRut(rut)

        if (rutLimpio.length < 2) {
            return "El RUT es demasiado corto"
        }

        val rutNumero = rutLimpio.substring(0, rutLimpio.length - 1)

        if (!rutNumero.all { it.isDigit() }) {
            return "El RUT debe contener solo números y un dígito verificador"
        }

        val numero = rutNumero.toIntOrNull()
        if (numero == null || numero < 1000000) {
            return "El RUT no tiene un formato válido"
        }

        if (numero > 99999999) {
            return "El RUT excede el rango válido"
        }

        if (!validarRut(rut)) {
            return "El dígito verificador del RUT es incorrecto"
        }

        return null
    }
}

