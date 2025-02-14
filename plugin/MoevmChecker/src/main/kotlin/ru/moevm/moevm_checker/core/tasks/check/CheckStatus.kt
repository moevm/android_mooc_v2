package ru.moevm.moevm_checker.core.tasks.check

sealed class CheckStatus(val result: String) {
    class Failed(failedDescription: String) : CheckStatus(failedDescription)

    class Success(successDescription: String) : CheckStatus(successDescription)
}
