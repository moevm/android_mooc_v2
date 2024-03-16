package ru.moevm.moevm_checker.core.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.ui.courses_ui.CoursesContentViewImpl
import ru.moevm.moevm_checker.core.ui.no_main_file_ui.NoMainFileContentViewImpl
import ru.moevm.moevm_checker.core.ui.task_ui.TaskViewImpl

class MainToolWindow : ToolWindowFactory {

    private val validator = DepsInjector.providePluginFileValidatorImpl()

    override fun init(toolWindow: ToolWindow) { // вызывается при создании окна IDE
        println("mylog MainToolWindow init")
        super.init(toolWindow)
    }

    // вызывается при первом открытии окна плагина
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        DepsInjector.projectEnvironmentInfo.init(
            project.guessProjectDir()?.path ?: "error", // Нужно всегда выставлять корнем - папку с MainCourseFile
            ProjectRootManager.getInstance(project).projectSdk?.homePath ?: "error"
        )
        println("mylog MainToolWindow project is ${project.guessProjectDir()}")
        var currentContent: Content? = null
        when {
            validator.isMainCoursesFileValid() -> { /*prepare for courses state*/
                setCoursePanel(toolWindow).apply { currentContent = this }
            }
            validator.isTaskFileValid() -> { /*prepare for task state*/
                setTaskPanel(toolWindow).apply { currentContent = this }
            }
            else -> {
                setNoMainFilePanel(toolWindow) { /*create new MainCourses file*/
                    currentContent?.let { content ->
                        toolWindow.contentManager.removeContent(content, true)
                        setCoursePanel(toolWindow).apply { currentContent = this }
                    }
                }
            }
        }
    }

    private fun setNoMainFilePanel(toolWindow: ToolWindow, onResetCurrentViewToCourseView: () -> Unit): Content {
        val noMainFileWindowPanelData = NoMainFileContentViewImpl(onResetCurrentViewToCourseView).getDialogPanel()
        val content = ContentFactory.getInstance()
            .createContent(noMainFileWindowPanelData.dialogPanel, noMainFileWindowPanelData.panelName, false)
        toolWindow.contentManager.addContent(content)
        return content
    }

    private fun setCoursePanel(toolWindow: ToolWindow): Content {
        val courseWindowDialogPanelData = CoursesContentViewImpl().getDialogPanel()
        val content = ContentFactory.getInstance()
            .createContent(courseWindowDialogPanelData.dialogPanel, courseWindowDialogPanelData.panelName, false)
        toolWindow.contentManager.addContent(content)
        return content
    }

    private fun setTaskPanel(toolWindow: ToolWindow): Content {
        val taskWindowDialogPanelData = TaskViewImpl().getDialogPanel()
        val content = ContentFactory.getInstance()
            .createContent(taskWindowDialogPanelData.dialogPanel, taskWindowDialogPanelData.panelName, false)
        toolWindow.contentManager.addContent(content)
        return content
    }
}