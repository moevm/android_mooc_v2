package ru.moevm.moevm_checker.core.tasks.codetask

import java.io.File

fun interface AbstractCheckSystem {
    fun rutTests(taskFolder: File): CodeTestResult
}