package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.moevm.moevm_checker.core.tasks.TaskConstants
import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCodeCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskResult
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.gradle.GradleCommandLine
import ru.moevm.moevm_checker.core.utils.coroutine.catchWithLog
import java.io.File

class AndroidTaskCodeCheckSystem(
    private val jdkPath: String?,
    private val taskArgs: List<String>,
    private val verificator: TaskFilesHashVerificator,
) : AbstractCodeCheckSystem {

    private fun runInstrumentalTests(jdkPath: String, taskFolder: File): CodeTaskResult {
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }

    private fun runUnitTests(jdkPath: String, taskFolder: File): CodeTaskResult {
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "app:testDebugUnitTest")
        return gcl.launch()
    }

    override fun runTests(taskFolder: File): CodeTaskResult {
        if (jdkPath == null) {
            return CodeTaskResult(CheckResult.Error("JDK не найден"), "", "")
        }
        if (!verificator.verify(taskFolder)) {
            return CodeTaskResult(
                CheckResult.Error("Ошибка тестов"),
                "Не удалось подтвердить подлинность тестовых файлов.\n" +
                        "Если вы модифицировали файлы тестов задачи - уберите изменения и перезапустите проверку.\n" +
                        "Если нет - загрузите задачу снова или обратитесь к составителям.",
                "",
            )
        }
        val checkResults = mutableListOf<TaskResult>()
        val logcatCollector = AndroidLogcatCollector()
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        var cancelledResult: CodeTaskResult? = null
        var logcatCheckerState = ""
        if (taskArgs.isNotEmpty()) {
            logcatCollector
                .start(selector = { line ->
                    line.contains(TaskConstants.CHECKER_FLAG)
                })
                .catchWithLog { e ->
                    cancelledResult = CodeTaskResult(CheckResult.Error(getPrettyExceptionMessage(e)), "", "")
                }
                .onEach { str ->
                    logcatCheckerState = str
                }
                .launchIn(coroutineScope)
        }
        for (arg in taskArgs) {
            if (cancelledResult != null) {
                break
            }
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
        val probablyResultCode = logcatCheckerState.takeLastWhile { it != ' ' }
        return cancelledResult
            ?: if (checkResults.all { it.taskResult.result is CheckResult.Passed }) {
                CodeTaskResult(
                    CheckResult.Passed,
                    checkResults.joinToString("\n\n\n") { "${it.arg}:\n\n" + it.taskResult.stdout },
                    checkResults.joinToString("\n\n\n") {
                        if (it.taskResult.stderr.isBlank()) {
                            ""
                        } else {
                            "${it.arg}:\n\n" + it.taskResult.stderr
                        }
                    }.ifBlank { "" },
                    probablyResultCode
                )
            } else {
                CodeTaskResult(CheckResult.Failed, "", "Unknown error")
            }
    }

    private fun getPrettyExceptionMessage(e: Throwable?): String {
        val startMessage = "Error during test: "
        return if (e?.message?.contains("Cannot run program \"adb\"") == true) {
            startMessage + (e.message + ". Check \"adb\" path in you PATH or download it.")
        } else {
            "$startMessage ${e?.message}"
        }
    }

    inner class TaskResult(
        val arg: String,
        val taskResult: CodeTaskResult,
    )
}