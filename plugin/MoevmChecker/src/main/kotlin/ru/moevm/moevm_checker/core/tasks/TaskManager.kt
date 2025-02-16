package ru.moevm.moevm_checker.core.tasks

import com.intellij.ide.RecentProjectsManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import java.io.File
import kotlin.io.path.Path

class TaskManager(
    private val coursesRepository: CoursesRepository,
    private val projectConfigProvider: ProjectConfigProvider,
    private val uiDispatcher: CoroutineDispatcher
) {

    fun openTask(taskId: String): Flow<Unit> = flowSafe {
        val rootDir = projectConfigProvider.rootDir ?: return@flowSafe
        val (selectedCourse, _) = coursesRepository.findCourseAndTaskByTaskId(taskId) ?: return@flowSafe
        val taskName = getTaskFileNameByTaskId(taskId)
        val pathToTask = buildPath(rootDir, selectedCourse.name, taskName)
        println("open new task, path = $pathToTask")
        if (File(pathToTask).exists()) {
            ProjectManagerEx.getInstanceEx().openProjectAsync(Path(pathToTask))
            withContext(uiDispatcher) {
                RecentProjectsManager.getInstance().removePath(pathToTask)
            }
            emit(Unit)
        } else {
            return@flowSafe
        }
    }

    fun getTaskDescription(taskId: String): Flow<String> = flowSafe {
        val rootDir = projectConfigProvider.rootDir
        if (rootDir == null) {
            emit("")
            return@flowSafe
        }
        val courseAndTask = coursesRepository.findCourseAndTaskByTaskId(taskId)
        if (courseAndTask == null) {
            emit("")
            return@flowSafe
        }
        val selectedCourse = courseAndTask.first
        val taskName = getTaskFileNameByTaskId(taskId)
        val descriptionFile = File(buildPath(rootDir, selectedCourse.name, taskName, TASK_DESCRIPTION_NAME))
        if (descriptionFile.exists()) {
            emit(descriptionFile.readText())
        } else {
            emit("")
        }
    }

    companion object {
        const val TASK_DESCRIPTION_NAME = "task_description.md"

        @JvmStatic
        fun getTaskFileNameByTaskId(taskId: String): String = "task${taskId}"

        @JvmStatic
        private fun buildPath(vararg piece: String): String {
            return piece.fold("") { prev, new ->
                File(prev, new).path
            }
        }
    }
}