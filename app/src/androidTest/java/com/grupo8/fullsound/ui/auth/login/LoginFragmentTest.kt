package com.grupo8.fullsound.ui.auth.login

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.grupo8.fullsound.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TOP 3 TESTS CRÍTICOS - LoginFragment (Tests de recursos)
 */
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    // TEST 13: Recursos del layout existen
    @Test
    fun test13_loginFragment_recursosExisten() {
        val layoutId = context.resources.getIdentifier("fragment_login", "layout", context.packageName)
        assertTrue("Layout fragment_login debe existir", layoutId != 0)

        // Verificar que los IDs de los elementos existen
        val emailInputId = context.resources.getIdentifier("emailInput", "id", context.packageName)
        val passwordInputId = context.resources.getIdentifier("passwordInput", "id", context.packageName)
        val loginButtonId = context.resources.getIdentifier("loginButton", "id", context.packageName)

        assertTrue("emailInput debe existir", emailInputId != 0)
        assertTrue("passwordInput debe existir", passwordInputId != 0)
        assertTrue("loginButton debe existir", loginButtonId != 0)
    }

    // TEST 14: String resources existen
    @Test
    fun test14_loginFragment_stringsExisten() {
        val emailHint = context.resources.getIdentifier("email_or_username_hint", "string", context.packageName)
        val passwordHint = context.resources.getIdentifier("password_hint", "string", context.packageName)

        assertTrue("String email_or_username_hint debe existir", emailHint != 0)
        assertTrue("String password_hint debe existir", passwordHint != 0)
    }

    // TEST 15: Tema Theme.FullSound existe
    @Test
    fun test15_temaFullSoundExiste() {
        // Usar R.style directamente para evitar problemas con puntos en getIdentifier
        val themeId = R.style.Theme_FullSound
        assertTrue("Tema Theme.FullSound debe existir", themeId != 0)

        // Verificar que se puede obtener el tema
        val theme = context.resources.newTheme()
        theme.applyStyle(themeId, true)
        assertNotNull("El tema debe ser aplicable", theme)
    }
}





