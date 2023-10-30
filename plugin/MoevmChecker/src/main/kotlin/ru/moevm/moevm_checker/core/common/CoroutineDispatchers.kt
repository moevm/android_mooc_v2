package ru.moevm.moevm_checker.core.common

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val worker: CoroutineDispatcher
    val ui: CoroutineDispatcher
}