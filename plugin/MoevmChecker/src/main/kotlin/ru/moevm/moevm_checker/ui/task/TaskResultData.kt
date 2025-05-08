package ru.moevm.moevm_checker.ui.task

import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult

data class TaskResultData(
    val result: CheckResult,
    val stdout: String,
    val stderr: String,
    val taskResultCode: String? = null,
)
