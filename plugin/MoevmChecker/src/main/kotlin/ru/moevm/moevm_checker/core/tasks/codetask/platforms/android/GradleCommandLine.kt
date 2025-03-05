package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskResult
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.GradleConstants.GRADLE_WRAPPER_UNIX
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.GradleConstants.GRADLE_WRAPPER_WIN

class GradleCommandLine(
    private val cmd: GeneralCommandLine,
    private val command: String
) {

    fun launch(timeoutMs: Int = TASK_TIMEOUT_MS): CodeTaskResult {
        val output = try {
            val handler = CapturingProcessHandler(cmd)
            handler.runProcess(timeoutMs)
        } catch (e: ExecutionException) {
            return CodeTaskResult(CheckResult.Error("Test launch error"), "", "${e.message}")
        }

        val stderr = output.stderr
        val stdout = output.stdout
        if (!isTaskPassed(stderr, stdout)) {
            return CodeTaskResult(CheckResult.Failed, stdout, stderr)
        }

        return CodeTaskResult(CheckResult.Passed, stdout, stderr)
    }

    private fun isTaskPassed(
        stderr: String,
        stdout: String
    ): Boolean {
        return when {
            stderr.isNotEmpty() && stdout.isEmpty() -> false
            GradleStderrAnalyzer.isStderrContainsError(stderr) -> false
            !stdout.contains(command) -> false
            else -> true
        }
    }

    companion object {
        private const val TASK_TIMEOUT_MS = 60 * 1000
        private const val JAVA_HOME = "JAVA_HOME"
        private const val JAVA_OPTS = "JAVA_OPTS"
        private const val UTF_8_ENCODING_PARAM = "-Dfile.encoding=UTF-8"

        fun create(
            basePath: String,
            projectJdkPath: String,
            command: String,
            vararg additionalParams: String
        ): GradleCommandLine {
            val projectPath = FileUtil.toSystemDependentName(basePath)
            val javaOpts = calculateJavaOpts()
            val cmd = GeneralCommandLine()
                .withEnvironment(JAVA_HOME, projectJdkPath)
                .withEnvironment(JAVA_OPTS, javaOpts)
                .withWorkDirectory(FileUtil.toSystemDependentName(basePath))
                .withExePath(
                    if (SystemInfo.isWindows) FileUtil.join(
                        projectPath,
                        GRADLE_WRAPPER_WIN
                    ) else "./$GRADLE_WRAPPER_UNIX"
                )
                .withParameters(command)
                .withCharset(Charsets.UTF_8)
                .withParameters(*additionalParams)

            return GradleCommandLine(cmd, command)
        }

        private fun calculateJavaOpts(): String {
            val javaOpts = System.getenv(JAVA_OPTS) ?: return UTF_8_ENCODING_PARAM
            return when {
                javaOpts.isEmpty() -> UTF_8_ENCODING_PARAM
                // don't override user's JAVA_OPTS and -Dfile.encoding
                javaOpts.contains("file.encoding") -> javaOpts
                else -> "$javaOpts $UTF_8_ENCODING_PARAM"
            }
        }
    }
}