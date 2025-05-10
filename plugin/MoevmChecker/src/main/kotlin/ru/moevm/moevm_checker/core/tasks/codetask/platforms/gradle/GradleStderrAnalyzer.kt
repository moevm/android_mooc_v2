package ru.moevm.moevm_checker.core.tasks.codetask.platforms.gradle

object GradleStderrAnalyzer {
    fun isStderrContainsError(stderr: String): Boolean {
        for (error in GradleCheckErrors.ERRORS) {
            if (error in stderr) {
                return true
            }
        }
        return false
    }
}