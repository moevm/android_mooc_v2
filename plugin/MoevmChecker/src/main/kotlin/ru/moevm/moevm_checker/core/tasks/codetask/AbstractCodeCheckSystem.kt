package ru.moevm.moevm_checker.core.tasks.codetask

import java.io.File

fun interface AbstractCodeCheckSystem {
    fun rutTests(taskFolder: File): CodeTaskResult
}