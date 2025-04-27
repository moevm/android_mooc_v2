package com.example.textview_edittext_button

import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.checker_lib.Checker
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest : Checker() {
    override val activityClass: Class<out AppCompatActivity> = MainActivity::class.java
    override val layoutId: Int = R.id.main
}