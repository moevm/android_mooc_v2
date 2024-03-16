package ru.moevm.moevm_checker.core.task

import ru.moevm.moevm_checker.android.tasks.AndroidTask
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.plugin_utils.Utils
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo

class TaskBuilder(
    private val environment: ProjectEnvironmentInfo = DepsInjector.projectEnvironmentInfo
) {
    fun buildTask(taskPlatformType: TaskPlatformType, taskId: String): Task? {
        val rootDir = environment.rootDir
        val pathToTask = Utils.buildFilePath(rootDir)
        return when (taskPlatformType) {
            TaskPlatformType.ANDROID -> {
                AndroidTask(pathToTask, taskId, environment.jdkPath)
            }
            else -> null
        }
    }
}