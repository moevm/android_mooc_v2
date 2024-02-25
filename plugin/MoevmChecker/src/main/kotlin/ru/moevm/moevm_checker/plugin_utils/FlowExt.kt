package ru.moevm.moevm_checker.plugin_utils

import com.android.tools.idea.gradle.project.sync.stackTraceAsMultiLineMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.catchLog(): Flow<T> {
    return this.catch { exception ->
        println(
            "exception: ${exception.message}\n${
                exception.stackTraceAsMultiLineMessage().joinToString(separator = "\n")
            }"
        )
    }
}