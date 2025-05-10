package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import kotlinx.coroutines.Dispatchers
import ru.moevm.moevm_checker.core.tasks.TaskConstants
import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCodeCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskResult
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.gradle.GradleCommandLine
import java.io.File

class AndroidTaskCodeCheckSystem(
    private val jdkPath: String?,
    private val taskArgs: List<String>,
) : AbstractCodeCheckSystem {

    private fun runInstrumentalTests(jdkPath: String, taskFolder: File): CodeTaskResult {
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }

    private fun runUnitTests(jdkPath: String, taskFolder: File): CodeTaskResult {
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "app:testDebugUnitTest")
        return gcl.launch()
    }

    override fun rutTests(taskFolder: File): CodeTaskResult {
        if (jdkPath == null) {
            return CodeTaskResult(CheckResult.Error("JDK Not found"), "", "")
        }
        val checkResults = mutableListOf<TaskResult>()
        val logcatCollector = AndroidLogcatCollector()
        if (taskArgs.isNotEmpty()) {
            logcatCollector.start(selector = { line ->
                line.contains(TaskConstants.CHECKER_FLAG)
            }, Dispatchers.IO)
        }
        for (arg in taskArgs) {
            when (arg) {
                "unit_test" -> {
                    val result = runUnitTests(jdkPath, taskFolder)
                    if (result.result is CheckResult.Failed || result.result is CheckResult.Error) {
                        return result
                    }
                    checkResults.add(TaskResult("Unit tests", result))
                }
                "instrumental_test" -> {
                    val result = runInstrumentalTests(jdkPath, taskFolder)
                    if (result.result is CheckResult.Failed || result.result is CheckResult.Error) {
                        return result
                    }
                    checkResults.add(TaskResult("Instrumental tests", result))
                }
            }
        }
        val probablyResultCode = logcatCollector.collect().lastOrNull()?.takeLastWhile { it != ' ' }
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
                probablyResultCode
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