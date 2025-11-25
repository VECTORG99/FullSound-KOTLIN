package com.grupo8.fullsound.ui.auth.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grupo8.fullsound.MainActivity
import com.grupo8.fullsound.R
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TOP 3 TESTS CRÍTICOS - LoginFragment
 */
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(500)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    // TEST 13: Elementos UI visibles
    @Test
    fun test13_loginFragment_elementosUIVisibles() {
        onView(withId(R.id.txt_titulo)).check(matches(isDisplayed()))
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    // TEST 14: Botón deshabilitado por defecto
    @Test
    fun test14_loginButton_deshabilitadoPorDefecto() {
        onView(withId(R.id.loginButton)).check(matches(not(isEnabled())))
    }

    // TEST 15: Credenciales válidas habilitan botón
    @Test
    fun test15_credencialesValidas_habilitanBoton() {
        onView(withId(R.id.emailEditText))
            .perform(typeText("test@test.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("password123"), closeSoftKeyboard())

        Thread.sleep(200)

        onView(withId(R.id.loginButton)).check(matches(isEnabled()))
    }
}

