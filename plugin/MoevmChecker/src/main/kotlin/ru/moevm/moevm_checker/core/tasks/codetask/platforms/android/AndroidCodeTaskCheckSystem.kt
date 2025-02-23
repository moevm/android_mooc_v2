package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTestResult
import java.io.File

class AndroidCodeTaskCheckSystem(
    private val jdkPath: String?,
) : AbstractCheckSystem {

    private fun runInstrumentalTests(jdkPath: String, taskFolder: File): CodeTestResult {
        val gcl = GradleCommandLine.create(taskFolder.path, jdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }

    override fun rutTests(taskFolder: File): CodeTestResult {
        if (jdkPath == null) {
            return CodeTestResult(isSuccess = false, CheckResult.Error("JDK Not found"), "", "")
        }
        val unitTestResult = runInstrumentalTests(jdkPath, taskFolder)
        // TODO Добавить возможность запускать несколько тестов (юнит-тесты)?
        return unitTestResult
    }
}