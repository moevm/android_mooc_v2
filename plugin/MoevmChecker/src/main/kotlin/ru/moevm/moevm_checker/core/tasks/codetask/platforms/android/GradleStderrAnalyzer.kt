package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

object GradleStderrAnalyzer {
    fun isStderrContainsError(stderr: String): Boolean {
        for (error in AndroidCheckErrors.ERRORS) {
            if (error in stderr) {
                return true
            }
        }
        return false
    }
}