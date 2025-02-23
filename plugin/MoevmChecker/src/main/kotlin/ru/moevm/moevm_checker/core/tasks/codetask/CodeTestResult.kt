package ru.moevm.moevm_checker.core.tasks.codetask

class CodeTestResult(
    val isSuccess: Boolean,
    val result: CheckResult,
    val stdout: String,
    val stderr: String
)
