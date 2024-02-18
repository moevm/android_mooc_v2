package ru.moevm.moevm_checker.core.ui.task_ui

import kotlinx.coroutines.*
import ru.moevm.moevm_checker.android.tasks.GradleCommandLine
import ru.moevm.moevm_checker.android.tasks.GradleOutput
import ru.moevm.moevm_checker.core.di.DepsInjector

class TaskPresenterImpl(
    override val taskView: TaskView,
    private val ioDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().worker,
    private val uiDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().ui
) : TaskPresenter {
    private var prevTaskCheck: Job? = null

    private val environment = DepsInjector.projectEnvironmentInfo
    override fun onCheckClicked() {
        prevTaskCheck?.cancel()
        prevTaskCheck = CoroutineScope(ioDispatcher).launch {
            val output = runTestTask()
            val testResult = buildString {
                append("Result:\n")
                append(output?.firstMessage + "\n\n")
            }
            val testOutput = buildString {
                append("Stdout:\n")
                append(output?.stdout + "\n\n")
            }
            val testError = buildString {
                append("Stderr:\n")
                append(output?.stderr + "\n\n")
            }

            withContext(uiDispatcher) {
                taskView.refreshUiState(
                    isLoadingLabelVisible = false,
                    taskResultText = testResult,
                    taskStdoutText = testOutput,
                    taskStderrText = testError
                )
            }
        }
    }

    private fun runTestTask(): GradleOutput? {
        val basePath = environment.rootDir
        val projectJdkPath = environment.jdkPath
        val gcl = GradleCommandLine.create(basePath, projectJdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }
}