package ru.moevm.moevm_checker.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.tasks.TaskConstants
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodePlatform
import ru.moevm.moevm_checker.dagger.DaggerPluginComponent
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.navigation.ContentNavigationController
import ru.moevm.moevm_checker.utils.Utils
import java.io.File

class MainToolWindow : ToolWindowFactory {

    private lateinit var pluginComponent: PluginComponent
    private var contentNavigationController: ContentNavigationController? = null

    override fun init(toolWindow: ToolWindow) { // вызывается при создании окна IDE
        println("MainToolWindow init")
        super.init(toolWindow)
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectConfig = ProjectConfigProvider(project)
        pluginComponent = DaggerPluginComponent.factory().create(
            projectConfig,
            uiDispatcher = Dispatchers.Swing,
            ioDispatcher = Dispatchers.IO,
            workDispatcher = Dispatchers.Default,
        )
        contentNavigationController = ContentNavigationController(
            toolWindow.contentManager,
            pluginComponent,
        )

        // TODO: добавить предзагрузку данных пользователя вместо авторизации?
        val rootDir = projectConfig.rootDir
        when {
            rootDir != null && isTaskEnvironmentExisted(rootDir) -> {
                val taskReference = extractCourseIdAndTaskId(rootDir)
                val coursesRepository = pluginComponent.coursesRepository

                // try again with something better, life-aware scope?
                coursesRepository.findTaskByReferenceFlow(taskReference)
                    .onEach { task ->
                        val courseTaskPlatform = task?.courseTaskPlatform
                        withContext(Dispatchers.Swing) {
                            openPanelByTaskPlatform(courseTaskPlatform, taskReference)
                        }
                    }
                    .launchIn(CoroutineScope(Dispatchers.IO))
            }
            else -> {
                contentNavigationController?.setAuthContent()
            }
        }

        println("Project is ${project.guessProjectDir()}")
    }

    private fun openPanelByTaskPlatform(
        courseTaskPlatform: String?,
        taskReference: TaskReference
    ) {
        when (courseTaskPlatform) {
            TaskCodePlatform.ANDROID.type -> {
                contentNavigationController?.setAndroidTaskContent(taskReference)
            }

            TaskCodePlatform.KOTLIN.type -> {
                contentNavigationController?.setKotlinTaskContent(taskReference)
            }
        }
    }

    private fun isTaskEnvironmentExisted(path: String): Boolean {
        val taskFile = File(path, TaskConstants.TASK_FILE_NAME)
        return if (Utils.isFileReadable(taskFile)) {
            val reader = taskFile.bufferedReader()
            var isTaskEnvironmentValid: Boolean
            try {
                val isCourseIdFound = reader.readLine().startsWith(TaskConstants.COURSE_ID_FOR_TASK_FILE)
                val isTaskIdFound = reader.readLine().startsWith(TaskConstants.TASK_ID_FOR_TASK_FILE)
                isTaskEnvironmentValid = isCourseIdFound && isTaskIdFound
            } catch (e: Exception) {
                isTaskEnvironmentValid = false
            } finally {
                reader.close()
            }

            isTaskEnvironmentValid
        } else {
            false
        }
    }

    private fun extractCourseIdAndTaskId(path: String): TaskReference {
        val taskFile = File(path, TaskConstants.TASK_FILE_NAME)
        val reader = taskFile.bufferedReader()
        val courseId = reader.readLine().drop(TaskConstants.COURSE_ID_FOR_TASK_FILE.length)
        val taskId = reader.readLine().drop(TaskConstants.TASK_ID_FOR_TASK_FILE.length)
        return TaskReference(courseId, taskId)
    }
}