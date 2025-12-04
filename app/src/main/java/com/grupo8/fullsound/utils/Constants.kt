package com.grupo8.fullsound.utils

/**
 * Constantes de la aplicación
 */
object Constants {
    /**
     * IVA (Impuesto al Valor Agregado) en Chile: 19%
     */
    const val IVA_PERCENTAGE = 0.19

    /**
     * Calcula el IVA de un monto
     * @param subtotal El subtotal sin IVA
     * @return El monto del IVA
     */
    fun calcularIva(subtotal: Double): Double {
        return subtotal * IVA_PERCENTAGE
    }

    /**
     * Calcula el total incluyendo IVA
     * @param subtotal El subtotal sin IVA
     * @return El total con IVA incluido
     */
    fun calcularTotalConIva(subtotal: Double): Double {
        return subtotal + calcularIva(subtotal)
    }
}

