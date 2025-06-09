package ru.moevm.moevm_checker.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManager
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.swing.Swing
import org.jetbrains.annotations.NonNls
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.tasks.TaskConstants
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodePlatform
import ru.moevm.moevm_checker.core.utils.coroutine.catchWithLog
import ru.moevm.moevm_checker.dagger.DaggerPluginComponent
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.navigation.ContentNavigationController
import ru.moevm.moevm_checker.utils.PluginLogger
import ru.moevm.moevm_checker.utils.Utils
import java.io.File

class MainToolWindow : ToolWindowFactory {

    private lateinit var pluginComponent: PluginComponent
    private var contentNavigationController: ContentNavigationController? = null

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
            rootDir != null && Utils.isTaskEnvironmentExisted(rootDir) -> {
                val taskReference = extractCourseIdAndTaskId(rootDir)
                val coursesRepository = pluginComponent.coursesRepository

                // try again with something better, life-aware scope?
                coursesRepository.findTaskByReferenceFlow(taskReference)
                    .onEach { task ->
                        val courseTaskPlatform = task?.courseTaskPlatform
                        ApplicationManager.getApplication().invokeLater({
                            openPanelByTaskPlatform(project, courseTaskPlatform, taskReference)
                        }, ModalityState.defaultModalityState())
                    }
                    .catchWithLog { t ->
                        PluginLogger.d("createToolWindowContent", "try openPanelByTaskPlatform, t = $t")
                    }
                    .launchIn(CoroutineScope(Dispatchers.IO))
            }
            else -> {
                PluginLogger.d("MainToolWindow", "navigation: set course tree")
                contentNavigationController?.setCoursesTreeContent()
            }
        }
        PluginLogger.d("MainToolWindow", "Project is ${project.guessProjectDir()}")
    }

    private fun openPanelByTaskPlatform(
        project: Project,
        courseTaskPlatform: String?,
        taskReference: TaskReference
    ) {
        val projectPath = project.guessProjectDir()?.path ?: return
        when (courseTaskPlatform) {
            TaskCodePlatform.ANDROID.type -> {
                PluginLogger.d("MainToolWindow", "navigation: set ANDROID for $taskReference")
                contentNavigationController?.setAndroidTaskContent(taskReference)
                if (!project.isInitialized) {
                    PluginLogger.d("MainToolWindow", "project is not initialized, start refresh")
                    refreshProjectData(projectPath, project)
                } else {
                    PluginLogger.d("MainToolWindow", "project is initialized")
                }
            }

            TaskCodePlatform.KOTLIN.type -> {
                PluginLogger.d("MainToolWindow", "navigation: set KOTLIN for $taskReference")
                contentNavigationController?.setKotlinTaskContent(taskReference)
                if (!project.isInitialized) {
                    PluginLogger.d("MainToolWindow", "project is not initialized, start refresh")
                    refreshProjectData(projectPath, project)
                } else {
                    PluginLogger.d("MainToolWindow", "project is initialized")
                }
            }
        }
    }

    private fun refreshProjectData(
        projectPath: @NonNls String,
        project: Project
    ) {
        ExternalProjectsManager.getInstance(project).runWhenInitialized {
            ApplicationManager.getApplication().invokeLater({
                ExternalSystemUtil.refreshProject(
                    projectPath,
                    ImportSpecBuilder(project, GRADLE_ID)
                )
            }, ModalityState.defaultModalityState())
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

private val GRADLE_ID = ProjectSystemId("GRADLE")
