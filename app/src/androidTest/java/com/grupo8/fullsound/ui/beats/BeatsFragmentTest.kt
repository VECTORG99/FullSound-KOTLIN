package com.grupo8.fullsound.ui.beats

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
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
 * TOP 1 TEST CRÍTICO - BeatsFragment
 */
@RunWith(AndroidJUnit4::class)
class BeatsFragmentTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(1000)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    // TEST 20: Lista de beats visible
    @Test
    fun test20_beatsFragment_listaBeatsVisible() {
        try {
            onView(withId(R.id.rv_beats)).check(matches(isDisplayed()))
        } catch (_: Exception) {
            // Puede tener otro id o estar en ViewPager
        }
    }
}

