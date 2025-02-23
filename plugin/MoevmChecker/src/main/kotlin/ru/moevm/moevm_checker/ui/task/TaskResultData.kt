package ru.moevm.moevm_checker.ui.task

import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult

data class TaskResultData(
    val taskResult: CheckResult,
    val taskStdoutText: String,
    val taskStderrText: String,
)
