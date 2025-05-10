package ru.moevm.moevm_checker.core.tasks.codetask.platforms.kotlin

import ru.moevm.moevm_checker.core.tasks.TaskConstants
import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCodeCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskResult
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.gradle.GradleCommandLine
import java.io.File

class KotlinTaskCodeCheckSystem(
    private val jdkPath: String?,
    private val taskArgs: List<String>,
): AbstractCodeCheckSystem {

    private fun runUnitTests(jdkPath: String, taskFolder: File): CodeTaskResult {
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "clean", "test")
        return gcl.launch()
    }

    override fun rutTests(taskFolder: File): CodeTaskResult {
        if (jdkPath == null) {
            return CodeTaskResult(CheckResult.Error("JDK Not found"), "", "")
        }
        val checkResults = mutableListOf<TaskResult>()

        for (arg in taskArgs) {
            when (arg) {
                "unit_test" -> {
                    val result = runUnitTests(jdkPath, taskFolder)
                    if (result.result is CheckResult.Failed || result.result is CheckResult.Error) {
                        return CodeTaskResult(result.result, withHiddenCode(result.stdout), result.stderr)
                    }
                    checkResults.add(TaskResult("Unit tests", result))
                }
            }
        }

        val codeCollector = KotlinStdoutCodeCollector()
        return if (checkResults.all { it.taskResult.result is CheckResult.Passed }) {
            CodeTaskResult(
                CheckResult.Passed,
                extractStdoutAndHideCode(checkResults),
                checkResults.joinToString("\n\n\n") {
                    if (it.taskResult.stderr.isBlank()) {
                        ""
                    } else {
                        "${it.arg}:\n\n" + it.taskResult.stderr
                    }
                }.ifBlank { "" },
                checkResults.joinToString(separator = "") { codeCollector.collectCode(it.taskResult.stdout).toString() }
            )
        } else {
            CodeTaskResult(CheckResult.Failed, "", "Unknown error")
        }
    }

    private fun extractStdoutAndHideCode(checkResults: MutableList<TaskResult>): String {
        return checkResults.joinToString( "\n\n\n") {
            "${it.arg}:\n\n" + withHiddenCode(it.taskResult.stdout)
        }
    }

    private fun withHiddenCode(stdout: String): String {
        val index = stdout.indexOf(TaskConstants.CHECKER_FLAG)
        if (index == -1) {
            return stdout
        }
        val indexOfNewLine = stdout.indexOf("\n", index)
        return stdout.removeRange(index, indexOfNewLine)
    }

    inner class TaskResult(
        val arg: String,
        val taskResult: CodeTaskResult,
    )
}