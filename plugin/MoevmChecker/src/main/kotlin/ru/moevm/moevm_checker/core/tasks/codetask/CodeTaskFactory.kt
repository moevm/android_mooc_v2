package ru.moevm.moevm_checker.core.tasks.codetask

import ru.moevm.moevm_checker.core.data.course.TaskFileHash
import ru.moevm.moevm_checker.core.filesystem.HashFileVerificator
import ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper.AndroidTaskFileHashMapper
import ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper.KotlinTaskFileHashMapper
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.AndroidTaskCodeCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.TaskFilesHashVerificator
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.kotlin.KotlinTaskCodeCheckSystem

object CodeTaskFactory {

    fun create(
        environment: TaskCodeEnvironment,
        taskArgs: List<String>,
        hashFileVerificator: HashFileVerificator,
        taskFileHash: List<TaskFileHash>,
    ): CodeTaskWithCheckSystem {
        return when (environment) {
            is TaskCodeEnvironment.Android -> {
                CodeTaskWithCheckSystem(
                    environment.taskFolder,
                    AndroidTaskCodeCheckSystem(
                        environment.jdkPath,
                        taskArgs,
                        TaskFilesHashVerificator(
                            hashFileVerificator,
                            taskFileHash,
                            AndroidTaskFileHashMapper()
                        )
                    ),
                )
            }

            is TaskCodeEnvironment.Kotlin -> {
                CodeTaskWithCheckSystem(
                    environment.taskFolder,
                    KotlinTaskCodeCheckSystem(
                        environment.jdkPath,
                        taskArgs,
                        TaskFilesHashVerificator(
                            hashFileVerificator,
                            taskFileHash,
                            KotlinTaskFileHashMapper()
                        )
                    ),
                )
            }
        }
    }
}