package ru.moevm.moevm_checker.core.utils.coroutine

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("FunctionName")
fun <T> EventSharedFlow(): MutableSharedFlow<T> {
    return MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
}