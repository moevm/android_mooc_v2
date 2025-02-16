package ru.moevm.moevm_checker.core.tasks.codetask

import ru.moevm.moevm_checker.core.tasks.check.CheckStatus
import java.io.File

abstract class AbstractCodeTask(
    val taskFolder: File,
) {
    abstract fun execute() : CheckStatus
}