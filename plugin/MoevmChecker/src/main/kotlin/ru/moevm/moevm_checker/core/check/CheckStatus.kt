package ru.moevm.moevm_checker.core.check

sealed class CheckStatus(val message: String? = null) {
    class Failed(message: String) : CheckStatus(message)
    object Success : CheckStatus()
}