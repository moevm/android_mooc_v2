package ru.moevm.moevm_checker.core.task

import com.intellij.ide.RecentProjectsManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.check.CheckResult
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepository
import ru.moevm.moevm_checker.plugin_utils.Utils
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import java.io.File
import java.nio.file.NoSuchFileException
import kotlin.io.path.Path

class TaskManager(
    private val projectEnvironmentInfo: ProjectEnvironmentInfo,
    private val coursesInfoRepository: CoursesInfoRepository,
    private val taskBuilder: TaskBuilder,
    private val ioDispatcher: CoroutineDispatcher,
    private val uiDispatcher: CoroutineDispatcher
) {
    var currentTask: Task? = null

    fun openTask(taskId: String) {
        CoroutineScope(ioDispatcher).launch {
            val (selectedCourse, selectedTask) = coursesInfoRepository.findTaskAndCourseByTaskId(taskId) ?: return@launch
            val taskName = getTaskFileNameByTaskId(taskId)
            val pathToTask = Utils.buildFilePath(projectEnvironmentInfo.rootDir, selectedCourse.name, taskName)

            println("open new task, path: $pathToTask")
            if (File(pathToTask).exists()) {
                // TODO перенести в другую корутину Dispatchers
                // TODO открывает новый проект по заданному пути
                ProjectManagerEx.getInstanceEx().openProjectAsync(Path(pathToTask))

                // TODO удаляем только что открытый проект из "недавних"
                withContext(uiDispatcher) {
                    RecentProjectsManager.getInstance().removePath(pathToTask)
                }

                currentTask = taskBuilder.buildTask(
                    taskPlatformType = getTaskPlatformType(selectedTask.courseTaskPlatform),
                    taskId = selectedTask.id,
                )
            } else {
                throw NoSuchFileException("Cannot find $pathToTask")
            }
        }
    }

    private fun getTaskPlatformType(taskPlatformType: String?): TaskPlatformType {
        return when (taskPlatformType) {
            TaskPlatformType.ANDROID.platformName -> TaskPlatformType.ANDROID
            else -> TaskPlatformType.UNKNOWN
        }
    }

    fun runCurrentTask(): CheckResult? {
        if (currentTask == null) {
            throw IllegalStateException("Task shouldn't be null!")
        }

        return currentTask?.execute()
    }

    companion object {
        @JvmStatic
        fun getTaskFileNameByTaskId(taskId: String): String = "task${taskId}"
    }
}