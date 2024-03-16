package ru.moevm.moevm_checker.core.task

import ru.moevm.moevm_checker.core.check.CheckResult

abstract class Task(
    val pathToTask: String,
    val taskId: String
) {
    abstract fun execute(): CheckResult
}