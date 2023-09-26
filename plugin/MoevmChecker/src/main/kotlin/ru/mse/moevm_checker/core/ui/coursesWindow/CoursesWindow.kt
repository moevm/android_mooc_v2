package ru.mse.moevm_checker.core.ui.coursesWindow

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import ru.mse.moevm_checker.core.ui.BaseWindow

class CoursesWindow : BaseWindow {

    override fun getContent(): DialogPanel {
        return panel {}
    }
}