package ru.moevm.moevm_checker.core.ui.task_ui

import ru.moevm.moevm_checker.core.task.Task

interface TaskModel {
    val task: Task?
    val presenter: TaskPresenter
    var taskProblemHtml: String

    fun getTaskDescriptionInfo(): String?
}