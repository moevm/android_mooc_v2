package ru.moevm.moevm_checker.core.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.ui.courses_ui.CoursesWindow
import ru.moevm.moevm_checker.core.ui.task_ui.TaskViewImpl

class MainToolWindow : ToolWindowFactory {

    override fun init(toolWindow: ToolWindow) { // вызывается при создании окна IDE
        println("mylog MainToolWindow init")
        super.init(toolWindow)
    }

    // вызывается при первом открытии окна плагина
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // FIXME: Что делать, если path == null?
        DepsInjector.projectEnvironmentInfo.init(
            project.guessProjectDir()?.path ?: "error",
            ProjectRootManager.getInstance(project).projectSdk?.homePath ?: "error"
        )

        println("mylog MainToolWindow project is ${project.guessProjectDir()}")

        val courseWindowDialogPanelData = CoursesWindow().getWindowContent().getDialogPanel()
        val taskWindowDialogPanelData = TaskViewImpl().getDialogPanel()

        toolWindow.contentManager.addContent(
            ContentFactory.getInstance()
                .createContent(courseWindowDialogPanelData.dialogPanel, courseWindowDialogPanelData.panelName, false)
        )
        toolWindow.contentManager.addContent(
            ContentFactory.getInstance()
                .createContent(taskWindowDialogPanelData.dialogPanel, taskWindowDialogPanelData.panelName, false)
        )
    }
}