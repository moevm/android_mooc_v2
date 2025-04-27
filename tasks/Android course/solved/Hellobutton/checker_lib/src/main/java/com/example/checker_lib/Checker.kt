package com.example.checker_lib

import android.content.pm.ActivityInfo
import android.util.Base64
import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.components.kautomator.component.common.views.UiView
import com.kaspersky.components.kautomator.component.text.UiButton
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
    val button by lazy { UiButton { withId(activityClass.`package`?.name ?: "", "button") } }

    @get:Rule
    val activityRule by lazy { ActivityScenarioRule(activityClass) }

    @Test
    fun isButtonValid() {
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

                step("Check button is valid") {
                    button {
                        isDisplayed()
                        hasText("Hello!")
                    }
                }

                step("Check main screen is visible") {
                    mainScreenRootView { isVisible() }
                }
            }
        results += "kdq2_31fz"
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