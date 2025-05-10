package ru.moevm.moevm_checker.core.tasks.codetask.platforms.kotlin

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
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "test")
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
                        return result
                    }
                    checkResults.add(TaskResult("Unit tests", result))
                }
            }
        }

        val codeCollector = KotlinStdoutCodeCollector()
        return if (checkResults.all { it.taskResult.result is CheckResult.Passed }) {
            CodeTaskResult(
                CheckResult.Passed,
                checkResults.joinToString( "\n\n\n") { "${it.arg}:\n\n" + it.taskResult.stdout },
                checkResults.joinToString("\n\n\n") {
                    if (it.taskResult.stderr.isBlank()) {
                        ""
                    } else {
                        "${it.arg}:\n\n" + it.taskResult.stderr }
                }.ifBlank { "" },
                // TODO Проверить
                checkResults.map { codeCollector.collectCode(it.taskResult.stdout) }.joinToString(separator = "")
            )
        } else {
            CodeTaskResult(CheckResult.Failed, "", "Unknown error")
        }
    }

    inner class TaskResult(
        val arg: String,
        val taskResult: CodeTaskResult,
    )
}