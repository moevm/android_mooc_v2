package ru.moevm.moevm_checker.core.tasks.codetask

import ru.moevm.moevm_checker.core.tasks.check.CheckStatus
import java.io.File

interface AbstractCodeTask {

    val taskFolder: File

    fun execute() : CheckStatus
}