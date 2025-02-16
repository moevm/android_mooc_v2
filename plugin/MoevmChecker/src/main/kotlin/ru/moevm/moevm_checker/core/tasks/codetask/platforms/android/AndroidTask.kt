package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import ru.moevm.moevm_checker.core.tasks.check.CheckStatus
import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCodeTask
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTestResult
import java.io.File

class AndroidTask(
    taskFolder: File,
    private val checkSystem: AndroidCodeTaskCheckSystem,
) : AbstractCodeTask(taskFolder) {
    override fun execute(): CheckStatus {
        val result = checkSystem.rutTests(taskFolder)
        val checkResult = if (result.isSuccess) {
            CheckStatus.Success(successDescription = extractResult(result))
        } else {
            CheckStatus.Failed(failedDescription = extractResult(result))
        }
        return checkResult
    }

    private fun extractResult(result: CodeTestResult?): String {
        val extractedResult = buildString {
            append("STDOUT:\n")
            append(result?.stdout ?: "")
            append("STDERR:\n")
            append(result?.stderr ?: "")
        }
        return extractedResult
    }
}