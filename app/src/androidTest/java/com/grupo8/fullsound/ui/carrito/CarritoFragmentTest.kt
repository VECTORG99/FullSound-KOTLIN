package com.grupo8.fullsound.ui.carrito

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TOP 2 TESTS CRÍTICOS - CarritoFragment (Tests de recursos)
 */
@RunWith(AndroidJUnit4::class)
class CarritoFragmentTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    // TEST 18: Recursos del layout carrito existen
    @Test
    fun test18_carritoFragment_recursosExisten() {
        val layoutId = context.resources.getIdentifier("fragment_carrito", "layout", context.packageName)
        assertTrue("Layout fragment_carrito debe existir", layoutId != 0)

        // Verificar que los IDs críticos existen
        val rvCarritoId = context.resources.getIdentifier("rv_carrito", "id", context.packageName)
        val cardTotalId = context.resources.getIdentifier("card_total", "id", context.packageName)
        val txtTotalId = context.resources.getIdentifier("txt_total", "id", context.packageName)

        assertTrue("rv_carrito debe existir", rvCarritoId != 0)
        assertTrue("card_total debe existir", cardTotalId != 0)
        assertTrue("txt_total debe existir", txtTotalId != 0)
    }

    // TEST 19: String resources del carrito existen
    @Test
    fun test19_carritoFragment_stringsExisten() {
        val carritoVacio = context.resources.getIdentifier("carrito_vacio", "string", context.packageName)
        val total = context.resources.getIdentifier("total", "string", context.packageName)

        assertTrue("String carrito_vacio debe existir", carritoVacio != 0)
        assertTrue("String total debe existir", total != 0)
    }
}

