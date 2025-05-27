package com.example.checker_lib

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.checker_lib.TestUtils.getIdFromString
import com.example.checker_lib.TestUtils.withOrientation
import com.kaspersky.components.kautomator.component.common.views.UiView
import com.kaspersky.components.kautomator.component.edit.UiEditText
import com.kaspersky.components.kautomator.component.text.UiButton
import com.kaspersky.components.kautomator.component.text.UiTextView
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.kakao.common.views.KView
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
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
    private val textView by lazy { UiTextView { withId(activityClass.`package`?.name ?: "", "textView") } }
    private val editText by lazy { UiEditText { withId(activityClass.`package`?.name ?: "", "editText") } }
    private val button by lazy { UiButton { withId(activityClass.`package`?.name ?: "", "button") } }
    private val linearLayout by lazy { UiView { withId(activityClass.`package`?.name ?: "", "linearLayout") } }

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    val activityRule by lazy { ActivityScenarioRule(activityClass) }

    @Test
    fun mainScreenContainsLinearLayoutAndViews() {
        before {  }
            .after {  }
            .run {
                step("Check main screen is visible") {
                    mainScreenRootView { isVisible() }
                }

                step("Check main screen contains LinearLayout") {
                    mainScreenRootView {
                        isDisplayed()
                        hasDescendant(withClassName(Matchers.equalTo(LinearLayout::class.java.name)))
                    }
                }

                step("Check main screen contains LinearLayout with TextView, EditText, Button") {
                    linearLayout {
                        isDisplayed()
                        textView { isDisplayed() }
                        editText { isDisplayed() }
                        button { isDisplayed() }
                    }
                }

                step("Check main screen contains ordered LinearLayout with TextView, EditText, Button") {
                    KView {
                        withId(getIdFromString(context, "linearLayout"))
                        withMatcher(
                            TestUtils.withExactOrder(
                                ViewMatchers.withId(getIdFromString(context, "textView")),
                                ViewMatchers.withId(getIdFromString(context, "editText")),
                                ViewMatchers.withId(getIdFromString(context, "button")),
                            )
                        )
                        withMatcher(withOrientation(LinearLayoutCompat.VERTICAL))
                    }.isVisible()
                }
            }
        results += "dkjl18d_3141"
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