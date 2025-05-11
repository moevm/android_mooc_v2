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
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
abstract class Checker : TestCase() {

    abstract val activityClass: Class<out AppCompatActivity>

    @get:IdRes
    abstract val layoutId: Int

    private val mainScreenRootView by lazy { KView { withId(layoutId) } }
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    val activityRule by lazy { ActivityScenarioRule(activityClass) }

    // Write your code here

    companion object {
        private var results: String = ""

        @AfterClass
        @JvmStatic
        fun tearDown(){
            Log.d("CHECKER", results)
        }
    }
}