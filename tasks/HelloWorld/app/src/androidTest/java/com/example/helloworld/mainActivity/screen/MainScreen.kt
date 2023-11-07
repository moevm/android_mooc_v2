package com.example.helloworld.mainActivity.screen

import android.widget.TextView
import com.example.helloworld.MainActivity
import com.example.helloworld.R
import com.kaspersky.components.kautomator.component.text.UiTextView
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.common.views.KView

object MainScreen : KScreen<MainScreen>() {
    override val layoutId: Int = R.layout.activity_main
    override val viewClass : Class<*> = MainActivity::class.java

    val mainScreenRootView = KView { withId(R.id.main_screen) }
    val textView = UiTextView { withId(viewClass.`package`?.name ?: "", "hello_world") }
}