package ru.moevm.moevm_checker.core.tasks

import com.intellij.ide.RecentProjectsManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import java.io.File
import kotlin.io.path.Path

interface TaskManager {
    fun openTask(courseId: String, taskId: String): Flow<Unit>
    fun getTaskDescription(courseId: String, taskId: String): Flow<String>
}

class TaskManagerImpl(
    private val coursesRepository: CoursesRepository,
    private val projectConfigProvider: ProjectConfigProvider,
    private val uiDispatcher: CoroutineDispatcher
) : TaskManager {

    override fun openTask(courseId: String, taskId: String): Flow<Unit> = flowSafe {
        val projectDir = projectConfigProvider.rootDir ?: return@flowSafe
        val (selectedCourse, _) = coursesRepository.findCourseAndTaskByIdFlow(courseId, taskId).last() ?: return@flowSafe
        val taskName = TaskConstants.getTaskFileNameByTaskId(taskId)
        val pathToTask = buildPath(projectDir, selectedCourse.name, taskName)
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

    override fun getTaskDescription(courseId: String, taskId: String): Flow<String> = flowSafe {
        val taskDir = projectConfigProvider.rootDir
        if (taskDir == null) {
            emit("")
            return@flowSafe
        }
        val courseAndTask = coursesRepository.findCourseAndTaskByIdFlow(courseId, taskId).last()
        if (courseAndTask == null) {
            emit("")
            return@flowSafe
        }
        val descriptionFile = File(buildPath(taskDir, TaskConstants.TASK_DESCRIPTION_NAME))
        if (descriptionFile.exists()) {
            emit(descriptionFile.readText())
        } else {
            emit("")
        }
    }

    companion object {
        @JvmStatic
        private fun buildPath(vararg piece: String): String {
            return piece.fold("") { prev, new ->
                File(prev, new).path
            }
        }
    }
}