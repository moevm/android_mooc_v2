package ru.moevm.moevm_checker.core.filesystem

import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodePlatform
import java.io.File

interface TaskFileLauncherPermissionStrategy {
    fun setLauncherAsExecutable(taskCodePlatform: TaskCodePlatform, taskFolder: File)
}

class TaskFileLauncherPermissionStrategyMacOS: TaskFileLauncherPermissionStrategy {
    override fun setLauncherAsExecutable(taskCodePlatform: TaskCodePlatform, taskFolder: File) {
        when (taskCodePlatform) {
            TaskCodePlatform.ANDROID -> {
                File(taskFolder.path, "gradlew").setExecutable(true)
            }
            TaskCodePlatform.KOTLIN -> {
                File(taskFolder.path, "gradlew").setExecutable(true)
            }
        }
    }
}

object TaskFileLauncherPermissionStrategyStub: TaskFileLauncherPermissionStrategy {
    override fun setLauncherAsExecutable(taskCodePlatform: TaskCodePlatform, taskFolder: File) = Unit
}
