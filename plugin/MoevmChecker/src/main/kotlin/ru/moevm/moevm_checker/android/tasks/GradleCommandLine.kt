package ru.moevm.moevm_checker.android.tasks

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import ru.moevm.moevm_checker.android.tasks.GradleConstants.GRADLE_WRAPPER_UNIX
import ru.moevm.moevm_checker.android.tasks.GradleConstants.GRADLE_WRAPPER_WIN
import java.util.concurrent.ExecutionException

class GradleCommandLine(
    private val cmd: GeneralCommandLine,
    private val taskName: String
) {
    fun launch(): GradleOutput? {
        val output = try {
            val handler = CapturingProcessHandler(cmd)
            handler.runProcess() // TODO: runProcessWithProgressIndicator
        }
        catch (e: ExecutionException) {
            // TODO: Добавить логирование ошибки
            return null
        }

        val stderr = output.stderr
        val stdout = output.stdout
        if (stderr.isNotEmpty() && output.stdout.isEmpty()) {
            return GradleOutput(false, listOf("Failed!!!"), stdout, stderr)
        }

        if (GradleStderrAnalyzer.tryToGetCheckResult(stderr) != null) {
            return GradleOutput(false, listOf("Failed!!!"), stdout, stderr)
        }

        if (!output.stdout.contains(taskName)) {
            return GradleOutput(false, listOf("Failed!!!"), stdout, stderr)
        }

        return GradleOutput(true, listOf("Passed!!!"), stdout, stderr)
    }

    companion object {
        private const val JAVA_HOME = "JAVA_HOME"
        private const val JAVA_OPTS = "JAVA_OPTS"
        private const val UTF_8_ENCODING_PARAM = "-Dfile.encoding=UTF-8"

        fun create(project: Project, command: String, vararg additionalParams: String): GradleCommandLine? {
            val basePath = project.basePath ?: return null
            val projectJdkPath = ProjectRootManager.getInstance(project).projectSdk?.homePath ?: return null
            val projectPath = FileUtil.toSystemDependentName(basePath)
            val javaOpts = calculateJavaOpts()
            val cmd = GeneralCommandLine()
                .withEnvironment(JAVA_HOME, projectJdkPath)
                .withEnvironment(JAVA_OPTS, javaOpts)
                .withWorkDirectory(FileUtil.toSystemDependentName(basePath))
                .withExePath(if (SystemInfo.isWindows) FileUtil.join(projectPath, GRADLE_WRAPPER_WIN) else "./$GRADLE_WRAPPER_UNIX")
                .withParameters(command)
                .withCharset(Charsets.UTF_8)
                .withParameters(*additionalParams)

            return GradleCommandLine(cmd, command)
        }

        private fun calculateJavaOpts() : String {
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