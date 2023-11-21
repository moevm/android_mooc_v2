package ru.moevm.moevm_checker.android.tasks

class GradleOutput(val isSuccess: Boolean, _messages: List<String>, val stdout: String, val stderr: String) {
    val messages = _messages.map { it.replace(System.getProperty("line.separator"), "\n") }

    val firstMessage: String = messages.firstOrNull { it.isNotBlank() } ?: "no output"
}