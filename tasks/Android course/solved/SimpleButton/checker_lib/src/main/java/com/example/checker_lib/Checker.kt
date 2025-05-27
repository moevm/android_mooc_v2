package com.example.checker_lib

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.checker_lib.TestUtils.getIdFromString
import com.kaspersky.components.kautomator.component.text.UiButton
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.kakao.common.views.KView
import org.hamcrest.Matchers
import org.junit.AfterClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class Checker : TestCase() {

    abstract val activityClass: Class<out AppCompatActivity>

    @get:IdRes
    abstract val layoutId: Int

    private val mainScreenRootView by lazy { KView { withId(layoutId) } }
    private val button by lazy { UiButton { withId(activityClass.`package`?.name ?: "", "button") } }

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    val activityRule by lazy { ActivityScenarioRule(activityClass) }

    @Test
    fun mainScreenContainsInteractiveButton() {
        before {  }
            .after {  }
            .run {
                step("Check main screen is visible") {
                    mainScreenRootView { isVisible() }
                }

                step("Check main screen contains Button") {
                    button {
                        isDisplayed()
                    }
                }

                step("Check main screen contains interactive button") {
                    button {
                        isEnabled()
                        hasText("Button")
                        click()
                        hasText("Button was pressed")
                    }
                }
            }
        results += "lvjai_23na_1"
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