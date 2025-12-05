package com.grupo8.fullsound.ui.beats

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TOP 1 TEST CRÍTICO - BeatsFragment (Test de recursos)
 */
@RunWith(AndroidJUnit4::class)
class BeatsFragmentTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    // TEST 20: Recursos del layout beats existen
    @Test
    fun test20_beatsFragment_recursosExisten() {
        val layoutBeatsId = context.resources.getIdentifier("fragment_beats", "layout", context.packageName)
        val layoutBeatsListaId = context.resources.getIdentifier("fragment_beats_lista", "layout", context.packageName)

        // Al menos uno de los layouts debe existir
        assertTrue("Layout fragment_beats o fragment_beats_lista debe existir",
            layoutBeatsId != 0 || layoutBeatsListaId != 0)

        // Verificar que item_beat existe para el RecyclerView
        val itemBeatId = context.resources.getIdentifier("item_beat", "layout", context.packageName)
        assertTrue("Layout item_beat debe existir", itemBeatId != 0)
    }
}

