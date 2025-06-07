package ru.moevm.moevm_checker.core.tasks.codetask

import java.io.File

class CodeTaskWithCheckSystem(
    val taskFolder: File,
    private val checkSystem: AbstractCodeCheckSystem,
) {

    fun execute(): CodeTaskResult {
        val result = checkSystem.runTests(taskFolder)
        return CodeTaskResult(
            result = result.result,
            stdout = result.stdout,
            stderr = result.stderr,
            probablyResultCode = result.probablyResultCode,
        )
    }
}