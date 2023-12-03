package ru.moevm.moevm_checker.core.ui.task_ui

import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import ru.moevm.moevm_checker.android.tasks.GradleCommandLine
import ru.moevm.moevm_checker.android.tasks.GradleOutput
import ru.moevm.moevm_checker.core.di.DepsInjector

class TaskPresenterImpl(
    override val taskView: TaskView,
    private val project: Project,
    private val ioDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().worker,
    private val uiDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().ui
) : TaskPresenter {
    private var prevTaskCheck: Job? = null

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
        val gcl = GradleCommandLine.create(project, "app:connectedDebugAndroidTest")
        return gcl?.launch()
    }
}