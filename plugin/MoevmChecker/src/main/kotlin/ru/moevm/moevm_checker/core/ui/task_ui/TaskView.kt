package ru.moevm.moevm_checker.core.ui.task_ui

interface TaskView {

    val presenter: TaskPresenter

    fun refreshUiState(
        isLoadingLabelVisible: Boolean,
        htmlTaskProblemText: String,
        taskResultText: String,
        taskStdoutText: String,
        taskStderrText: String
    )
}