package ru.moevm.moevm_checker.core.tasks.codetask.platforms.kotlin

class KotlinStdoutCodeCollector {

    fun collectCode(stdout: String): String? {
        if (stdout.isBlank()) {
            return null
        }
        val tokens = stdout.split("\n")
        return tokens.find { it.contains("CHECKER") }?.takeLastWhile { it != ' ' }
    }
}