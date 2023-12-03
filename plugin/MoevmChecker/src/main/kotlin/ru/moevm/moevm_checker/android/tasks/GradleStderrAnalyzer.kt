package ru.moevm.moevm_checker.android.tasks

import ru.moevm.moevm_checker.core.check.CheckResult
import ru.moevm.moevm_checker.core.check.CheckStatus
import ru.moevm.moevm_checker.core.check.CheckUtils

object GradleStderrAnalyzer {

    fun tryToGetCheckResult(stderr: String): CheckResult? {
        for (error in CheckUtils.ERRORS) {
            if (error in stderr) {
                return CheckResult(CheckStatus.Failed)
            }
        }
        return null
    }
}