package ru.moevm.moevm_checker.core.tasks.codetask

import ru.moevm.moevm_checker.core.tasks.TaskResult

class CodeTaskResult(
    result: CheckResult,
    val stdout: String,
    val stderr: String,
    probablyResultCode: String? = null,
) : TaskResult(result, probablyResultCode)
