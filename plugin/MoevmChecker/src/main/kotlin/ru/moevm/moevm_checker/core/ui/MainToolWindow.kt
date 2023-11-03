package ru.moevm.moevm_checker.core.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.ui.courses_ui.CoursesWindow
import ru.moevm.moevm_checker.core.ui.data.DialogPanelData

class MainToolWindow : ToolWindowFactory {

    override fun init(toolWindow: ToolWindow) { // вызывается при создании окна IDE
        println("mylog MainToolWindow init")
        super.init(toolWindow)
    }

    // вызывается при первом открытии окна плагина
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // FIXME: Что делать, если path == null? 
        DepsInjector.projectEnvironmentInfo.init(project.guessProjectDir()?.path ?: "error")

        println("mylog MainToolWindow project is ${project.guessProjectDir()}")
        val dialogPanelData: DialogPanelData? = if (isValidMainFile() || !isValidTaskFile()) {
            CoursesWindow(project.guessProjectDir()?.path ?: "").getWindowContent().getDialogPanel()
        } else {
            // TODO: Окно выполнения задания
            null
        }

        toolWindow.contentManager.addContent(ContentFactory.getInstance().createContent(dialogPanelData?.dialogPanel, dialogPanelData?.panelName, false))
    }

    private fun isValidMainFile(): Boolean {
        val validator = DepsInjector.provideCourseFileValidator()
        return validator.isMainCoursesFileValid()
    }

    private fun isValidTaskFile(): Boolean {
        val validator = DepsInjector.provideCourseFileValidator()
        return validator.isTaskFileValid()
    }
}