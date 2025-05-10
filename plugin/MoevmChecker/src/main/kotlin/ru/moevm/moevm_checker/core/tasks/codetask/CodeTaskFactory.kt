package ru.moevm.moevm_checker.core.tasks.codetask

import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.AndroidTaskCodeCheckSystem

object CodeTaskFactory {

    fun create(environment: TaskCodeEnvironment, taskArgs: List<String>): AbstractCodeTask {
        return when (environment) {
            is TaskCodeEnvironment.Android -> {
                CodeTaskWithCheckSystem(environment.taskFolder, AndroidTaskCodeCheckSystem(environment.jdkPath, taskArgs))
            }
        }
    }
}