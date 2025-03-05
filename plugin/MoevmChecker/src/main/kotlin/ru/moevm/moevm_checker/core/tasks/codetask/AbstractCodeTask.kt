package ru.moevm.moevm_checker.core.tasks.codetask

import java.io.File

interface AbstractCodeTask {

    val taskFolder: File

    fun execute() : CodeTaskResult
}