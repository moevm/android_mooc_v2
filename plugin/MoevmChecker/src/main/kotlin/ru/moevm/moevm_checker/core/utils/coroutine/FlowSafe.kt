package ru.moevm.moevm_checker.core.utils.coroutine

import com.android.tools.idea.gradle.project.sync.stackTraceAsMultiLineMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


fun <T> flowSafe(body: suspend FlowCollector<T>.() -> Unit) = flow {
    this.body()
}.catchLog()

fun <T> Flow<T>.catchLog(): Flow<T> {
    return this.catch { exception ->
        println(
            "exception: ${exception.message}\n${
                exception.stackTraceAsMultiLineMessage().joinToString(separator = "\n")
            }"
        )
    }
}