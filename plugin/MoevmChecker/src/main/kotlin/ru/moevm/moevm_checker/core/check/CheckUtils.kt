package ru.moevm.moevm_checker.core.check

object CheckUtils {
    private val COMPILATION_ERRORS = listOf("Compilation failed", "Compilation error")
    private val TEST_ERRORS = listOf("Build failed")

    val ERRORS = COMPILATION_ERRORS + TEST_ERRORS
}