package com.grupo8.fullsound.ui.auth.register

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TOP 2 TESTS CRÍTICOS - RegisterFragment (Tests de recursos)
 */
@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    // TEST 16: Recursos del layout existen
    @Test
    fun test16_registerFragment_recursosExisten() {
        val layoutId = context.resources.getIdentifier("fragment_register", "layout", context.packageName)
        assertTrue("Layout fragment_register debe existir", layoutId != 0)

        // Verificar que los IDs de los elementos existen
        val emailInputId = context.resources.getIdentifier("emailInput", "id", context.packageName)
        val usernameInputId = context.resources.getIdentifier("usernameInput", "id", context.packageName)
        val passwordInputId = context.resources.getIdentifier("passwordInput", "id", context.packageName)
        val registerButtonId = context.resources.getIdentifier("registerButton", "id", context.packageName)

        assertTrue("emailInput debe existir", emailInputId != 0)
        assertTrue("usernameInput debe existir", usernameInputId != 0)
        assertTrue("passwordInput debe existir", passwordInputId != 0)
        assertTrue("registerButton debe existir", registerButtonId != 0)
    }

    // TEST 17: String resources de registro existen
    @Test
    fun test17_registerFragment_stringsExisten() {
        val emailHint = context.resources.getIdentifier("email_hint", "string", context.packageName)
        val subtitulo = context.resources.getIdentifier("subtitulo_register", "string", context.packageName)

        assertTrue("String email_hint debe existir", emailHint != 0)
        assertTrue("String subtitulo_register debe existir", subtitulo != 0)
    }
}

