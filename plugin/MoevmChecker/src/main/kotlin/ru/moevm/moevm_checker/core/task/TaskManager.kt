package ru.moevm.moevm_checker.core.task

import kotlinx.coroutines.runBlocking
import ru.moevm.moevm_checker.core.check.CheckResult
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepository

class TaskManager(
    private val coursesInfoRepository: CoursesInfoRepository,
    private val taskBuilder: TaskBuilder
) {
    var currentTask: Task? = null

    fun setCurrentTask(taskId: String) {
        runBlocking { // FIXME: Заменить runBlocking на что-нибудь получше
            val (selectedCourse, selectedTask) = coursesInfoRepository.findTaskAndCourseByTaskId(taskId) ?: return@runBlocking

            currentTask = taskBuilder.buildTask(
                taskPlatformType = getTaskPlatformType(selectedTask.courseTaskPlatform),
                taskId = selectedTask.id,
                courseName = selectedCourse.id,
                taskName = selectedTask.id
            )
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
}