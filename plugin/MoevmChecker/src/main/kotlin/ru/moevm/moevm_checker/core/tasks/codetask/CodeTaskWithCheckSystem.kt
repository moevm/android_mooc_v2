package ru.moevm.moevm_checker.core.tasks.codetask

import java.io.File

class CodeTaskWithCheckSystem(
    override val taskFolder: File,
    private val checkSystem: AbstractCodeCheckSystem,
) : AbstractCodeTask {

    override fun execute(): CodeTaskResult {
        val result = checkSystem.rutTests(taskFolder)
        return CodeTaskResult(
            result = result.result,
            stdout = result.stdout,
            stderr = result.stderr,
            probablyResultCode = result.probablyResultCode,
        )
    }
}