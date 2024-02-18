package ru.moevm.moevm_checker.core.task

import ru.moevm.moevm_checker.core.check.CheckResult

abstract class Task(
    protected val pathToTask: String,
    protected val taskId: String
) {
    abstract fun execute(): CheckResult
}