package com.example.checker_lib

import android.content.pm.ActivityInfo
import android.util.Base64
import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.components.kautomator.component.text.UiTextView
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.kakao.common.views.KView
import org.junit.After
import org.junit.AfterClass

import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
abstract class Checker : TestCase() {

    abstract val activityClass: Class<out AppCompatActivity>

    @get:IdRes
    abstract val layoutId: Int

    val mainScreenRootView by lazy { KView { withId(layoutId) } }
    val textView by lazy { UiTextView { withId(activityClass.`package`?.name ?: "", "hello_world") } }

    @get:Rule
    val activityRule by lazy { ActivityScenarioRule(activityClass) }

    @Test
    fun screenDisplayed() {
        before {  }
            .after {  }
            .run {
                step("Check main screen is visible") {
                    mainScreenRootView { isVisible() }
                }

                step("Rotate to landscape") {
                    activityRule.scenario.onActivity {
                        it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

                step("TextView has text") {
                    textView {
                        hasText("Hello World!")
                    }
                }

                step("Check main screen is visible") {
                    mainScreenRootView { isVisible() }
                }
            }
        results += "1ga_2fa_xa"
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.helloworld", appContext.packageName)
        results += "e1t_asd_123"
    }


    @Test
    fun context() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        results += "5f_asd1_41_asfd"
    }

    companion object {
        private var results: String = ""

        @AfterClass
        @JvmStatic
        fun tearDown(){
            Log.d("CHECKER", results)
        }
    }
}