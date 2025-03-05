package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskResult
import java.io.File

class AndroidCodeTaskCheckSystem(
    private val jdkPath: String?,
    private val taskArgs: List<String>,
) : AbstractCheckSystem {

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
        val checkResults = mutableListOf<Pair<String, CodeTaskResult>>()
        for (arg in taskArgs) {
            when (arg) {
                "unit_test" -> {
                    val result = runUnitTests(jdkPath, taskFolder)
                    if (result.result is CheckResult.Failed || result.result is CheckResult.Error) {
                        return result
                    }
                    checkResults.add("Unit tests" to result)
                }
                "instrumental_test" -> {
                    val result = runInstrumentalTests(jdkPath, taskFolder)
                    if (result.result is CheckResult.Failed || result.result is CheckResult.Error) {
                        return result
                    }
                    checkResults.add("Instrumental tests" to result)
                }
            }
        }
        return if (checkResults.all { it.second.result is CheckResult.Passed }) {
            CodeTaskResult(
                CheckResult.Passed,
                checkResults.joinToString( "\n\n\n") { "${it.first}:\n\n" + it.second.stdout },
                checkResults.joinToString("\n\n\n") {
                    if (it.second.stderr.isBlank()) {
                        ""
                    } else {
                        "${it.first}:\n\n" + it.second.stderr }
                }.ifBlank { "" }
            )
        } else {
            CodeTaskResult(CheckResult.Failed, "", "Unknown error")
        }
    }
}