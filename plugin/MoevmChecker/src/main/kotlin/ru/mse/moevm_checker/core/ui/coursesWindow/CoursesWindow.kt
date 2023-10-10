package ru.mse.moevm_checker.core.ui.coursesWindow

import ru.mse.moevm_checker.core.di.DepsInjector
import ru.mse.moevm_checker.core.ui.BaseContent
import ru.mse.moevm_checker.core.ui.BaseWindow
import ru.mse.moevm_checker.core.ui.no_main_file.NoMainFileContent

class CoursesWindow(private val projectPath: String) : BaseWindow {
    override fun getWindowContent(): BaseContent {
        val validator = DepsInjector.provideCourseFileValidator()
        return if (!validator.isMainCoursesFileValid()) {
            NoMainFileContent(projectPath)
        } else {
            // TODO: Сделать окно курса
            throw IllegalArgumentException("")
        }
    }
}