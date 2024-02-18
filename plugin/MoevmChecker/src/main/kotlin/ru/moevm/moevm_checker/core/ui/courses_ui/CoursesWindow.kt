package ru.moevm.moevm_checker.core.ui.courses_ui

import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.ui.BaseContent
import ru.moevm.moevm_checker.core.ui.BaseWindow
import ru.moevm.moevm_checker.core.ui.no_main_file_ui.NoMainFileContentViewImpl

class CoursesWindow : BaseWindow {
    override fun getWindowContent(): BaseContent {
        val validator = DepsInjector.provideCourseFileValidator()
        return if (!validator.isMainCoursesFileValid()) {
            NoMainFileContentViewImpl()
        } else {
            CoursesContentViewImpl()
        }
    }
}