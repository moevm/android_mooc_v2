package ru.moevm.moevm_checker.core.tasks.codetask

import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.AndroidCodeTaskCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.AndroidTask

object CodeTaskFactory {

    fun create(environment: TaskCodeEnvironment): AbstractCodeTask {
        return when (environment) {
            is TaskCodeEnvironment.Android -> {
                AndroidTask(environment.taskFolder, AndroidCodeTaskCheckSystem(environment.jdkPath))
            }
        }
    }
}