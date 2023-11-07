package com.example.helloworld.mainActivity

import android.content.pm.ActivityInfo
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.helloworld.MainActivity
import com.example.helloworld.mainActivity.screen.MainScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest : TestCase() {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun screenDisplayed() {
        before {  }
            .after {  }
            .run {
                step("Check main screen is visible") {
                    MainScreen { mainScreenRootView { isVisible() } }
                }

                step("Rotate to landscape") {
                    activityRule.scenario.onActivity {
                        it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

                step("TextView has text") {
                    MainScreen { textView {
                        hasText("Hello World!")
                    } }
                }

                step("Check main screen is visible") {
                    MainScreen { mainScreenRootView { isVisible() } }
                }
            }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.helloworld", appContext.packageName)
    }


    @Test
    fun context() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }
}