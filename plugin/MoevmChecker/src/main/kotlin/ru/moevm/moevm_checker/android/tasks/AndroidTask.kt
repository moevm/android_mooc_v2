package ru.moevm.moevm_checker.android.tasks

import ru.moevm.moevm_checker.core.check.CheckResult
import ru.moevm.moevm_checker.core.check.CheckStatus
import ru.moevm.moevm_checker.core.task.Task

class AndroidTask(
    pathToTask: String,
    taskId: String,
    private val jdkPath: String?
) : Task(pathToTask, taskId) {
    override fun execute(): CheckResult {
        val result = runTestTask()
        val checkResult = if (result?.isSuccess == true) {
            CheckResult(CheckStatus.Success)
        } else {
            CheckResult(CheckStatus.Failed(extractResult(result)))
        }
        return checkResult
    }

    private fun extractResult(result: GradleOutput?): String {
        val extractedResult = buildString {
            append("STDOUT:\n")
            append(result?.stdout ?: "")
            append("STDERR:\n")
            append(result?.stderr ?: "")
        }
        return extractedResult
    }

    private fun runTestTask(): GradleOutput? {
        if (jdkPath == null) {
            return GradleOutput(isSuccess = false, listOf("JDK Not found"), "", "")
        }
        val gcl = GradleCommandLine.create(pathToTask, jdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }
}