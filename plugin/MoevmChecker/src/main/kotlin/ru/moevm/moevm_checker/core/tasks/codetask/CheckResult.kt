package ru.moevm.moevm_checker.core.tasks.codetask

sealed class CheckResult {
    data object Passed : CheckResult()

    data object Failed : CheckResult()

    class Error(val message: String) : CheckResult() {
        override fun toString(): String {
            return message
        }
    }
}
