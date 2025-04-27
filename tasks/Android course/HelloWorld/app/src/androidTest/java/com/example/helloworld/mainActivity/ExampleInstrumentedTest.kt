package com.example.helloworld.mainActivity

import androidx.appcompat.app.AppCompatActivity
import com.example.checker_lib.Checker
import com.example.helloworld.MainActivity
import com.example.helloworld.R

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class Test : Checker() {
    override val layoutId: Int = R.id.main_screen
    override val activityClass: Class<out AppCompatActivity> = MainActivity::class.java
}
