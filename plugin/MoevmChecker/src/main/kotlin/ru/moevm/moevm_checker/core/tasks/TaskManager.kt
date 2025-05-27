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
import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesItemType
import java.io.File
import kotlin.io.path.Path

interface TaskManager {
    fun openTask(taskReference: TaskReference): Flow<Unit>
    @Suppress("unused")
    fun getTaskDescription(taskReference: TaskReference): Flow<String>

    companion object {
        @JvmStatic
        fun isTaskOpenable(taskType: CoursesItemType) = when (taskType) {
            CoursesItemType.CODE_TASK -> true
            else -> false
        }

        @JvmStatic
        fun isTaskDownloadable(taskType: CoursesItemType) = when (taskType) {
            CoursesItemType.CODE_TASK -> true
            else -> false
        }

        @JvmStatic
        fun isTaskRemovable(taskType: CoursesItemType) = when (taskType) {
            CoursesItemType.CODE_TASK -> true
            else -> false
        }

        fun getCoursesItemType(taskType: String): CoursesItemType {
            return when (taskType) {
                CoursesItemType.CODE_TASK.type -> CoursesItemType.CODE_TASK
                else -> CoursesItemType.UNKNOWN
            }
        }
    }
}

class TaskManagerImpl(
    private val coursesRepository: CoursesRepository,
    private val projectConfigProvider: ProjectConfigProvider,
    private val uiDispatcher: CoroutineDispatcher
) : TaskManager {

    override fun openTask(taskReference: TaskReference): Flow<Unit> = flowSafe {
        val projectDir = projectConfigProvider.rootDir ?: return@flowSafe
        val (selectedCourse, selectedTask) = coursesRepository.findCourseAndTaskByReferenceFlow(taskReference).last() ?: return@flowSafe
        val pathToTask = buildPath(projectDir, selectedCourse.name, selectedTask.name)
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

    override fun getTaskDescription(taskReference: TaskReference): Flow<String> = flowSafe {
        val taskDir = projectConfigProvider.rootDir
        if (taskDir == null) {
            emit("")
            return@flowSafe
        }
        val courseAndTask = coursesRepository.findCourseAndTaskByReferenceFlow(taskReference).last()
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

    private companion object {
        @JvmStatic
        private fun buildPath(vararg piece: String): String {
            return piece.fold("") { prev, new ->
                File(prev, new).path
            }
        }
    }
}