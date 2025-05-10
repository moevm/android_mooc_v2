package ru.moevm.moevm_checker.core.tasks.codetask.platforms.kotlin

import ru.moevm.moevm_checker.core.tasks.TaskConstants

class KotlinStdoutCodeCollector {

    fun collectCode(stdout: String): String? {
        if (stdout.isBlank()) {
            return null
        }
        val tokens = stdout.split("\n")
        return tokens.find { it.contains(TaskConstants.CHECKER_FLAG) }?.takeLastWhile { it != ' ' }
    }
}