package ru.moevm.moevm_checker.core.ui.task_ui

interface TaskView {

    val presenter: TaskPresenter

    fun refreshUiState(
        isLoadingLabelVisible: Boolean,
        taskResultText: String,
        taskStdoutText: String,
        taskStderrText: String
    )
}