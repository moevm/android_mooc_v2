package ru.moevm.moevm_checker.core.tasks.codetask

import java.io.File

sealed class TaskCodeEnvironment(val taskFolder: File) {
    class Android(taskFolder: File, val jdkPath: String?) : TaskCodeEnvironment(taskFolder)
}