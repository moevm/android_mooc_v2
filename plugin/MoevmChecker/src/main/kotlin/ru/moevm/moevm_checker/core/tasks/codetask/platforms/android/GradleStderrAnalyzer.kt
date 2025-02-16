package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import ru.moevm.moevm_checker.core.tasks.check.CheckStatus

object GradleStderrAnalyzer {
    fun tryToGetCheckResult(stderr: String): CheckStatus? {
        for (error in AndroidCheckErrors.ERRORS) {
            if (error in stderr) {
                return CheckStatus.Failed(stderr)
            }
        }
        return null
    }
}