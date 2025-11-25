package com.grupo8.fullsound.ui.auth.register

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grupo8.fullsound.MainActivity
import com.grupo8.fullsound.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TOP 2 TESTS CRÍTICOS - RegisterFragment
 */
@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(500)

        try {
            onView(withId(R.id.registerText)).perform(click())
            Thread.sleep(300)
        } catch (_: Exception) {
            // Ya estamos en register
        }
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    // TEST 16: Elementos UI visibles
    @Test
    fun test16_registerFragment_elementosUIVisibles() {
        onView(withId(R.id.txt_titulo)).check(matches(isDisplayed()))
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()))
        onView(withId(R.id.usernameInput)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()))
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()))
    }

    // TEST 17: Datos válidos habilitan botón
    @Test
    fun test17_datosValidos_habilitanBotonRegistro() {
        onView(withId(R.id.emailEditText))
            .perform(typeText("test@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.usernameEditText))
            .perform(typeText("testuser"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("password123"), closeSoftKeyboard())

        Thread.sleep(200)

        onView(withId(R.id.registerButton)).check(matches(isEnabled()))
    }
}

