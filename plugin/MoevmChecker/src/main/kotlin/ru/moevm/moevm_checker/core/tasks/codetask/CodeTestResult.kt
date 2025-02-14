package ru.moevm.moevm_checker.core.tasks.codetask

class CodeTestResult(
    val isSuccess: Boolean,
    val messages: List<String>,
    val stdout: String,
    val stderr: String
) {
    val firstMessage = messages.firstOrNull() ?: "no output"
}
