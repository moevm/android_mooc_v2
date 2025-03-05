package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCodeTask
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskResult
import java.io.File

class AndroidTask(
    override val taskFolder: File,
    private val checkSystem: AndroidCodeTaskCheckSystem,
) : AbstractCodeTask {

    override fun execute(): CodeTaskResult {
        val result = checkSystem.rutTests(taskFolder)
        return CodeTaskResult(
            result = result.result,
            stdout = result.stdout,
            stderr = result.stderr,
        )
    }
}