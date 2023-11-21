package ru.moevm.moevm_checker.core.ui.task_ui

interface TaskPresenter {
    val taskView: TaskView

    fun onCheckClicked()
}