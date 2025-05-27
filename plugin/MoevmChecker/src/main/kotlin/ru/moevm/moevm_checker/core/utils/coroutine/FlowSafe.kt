package ru.moevm.moevm_checker.core.utils.coroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.moevm.moevm_checker.utils.PluginLogger

fun <T> flowSafe(body: suspend FlowCollector<T>.() -> Unit) = flow {
    this.body()
}.catchWithLog()

fun <T> Flow<T>.catchWithLog(catchBody: (e: Throwable) -> Unit = {}): Flow<T> {
    return this.catch { exception ->
        PluginLogger.d(
            "Flow catchLog",
            "EXCEPTION: ${exception.message}\n${
                exception.stackTrace.joinToString(separator = "\n")
            }"
        )
        catchBody(exception)
    }
}