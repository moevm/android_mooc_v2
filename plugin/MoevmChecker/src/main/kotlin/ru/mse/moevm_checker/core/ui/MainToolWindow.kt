package ru.mse.moevm_checker.core.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.DialogPanel
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.flow.onEach
import ru.mse.moevm_checker.core.di.DepsInjector
import ru.mse.moevm_checker.core.ui.splashWindow.SplashWindow

class MainToolWindow : ToolWindowFactory {

    override fun init(toolWindow: ToolWindow) { // вызывается при создании окна IDE
        println("mylog MainToolWindow init")
        super.init(toolWindow)
    }

    // вызывается при первом открытии окна плагина
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("mylog MainToolWindow project is ${project.guessProjectDir()}")
        val coursesInfoValidator = DepsInjector.provideCoursesInfoValidatorForFile()
        coursesInfoValidator.updateIsValidFileState(project.guessProjectDir()?.path ?: "")

        coursesInfoValidator.isValidPlaceState
        toolWindow.contentManager.addContent(ContentFactory.getInstance().createContent(SplashWindow(project.guessProjectDir()?.path ?: "").getContent(), "MyPanel", false))
    }

    private fun handleValidFileState() {

    }

}