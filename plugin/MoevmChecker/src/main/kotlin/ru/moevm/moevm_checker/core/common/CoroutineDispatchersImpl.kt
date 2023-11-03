package ru.moevm.moevm_checker.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing

class CoroutineDispatchersImpl : CoroutineDispatchers {
    override val worker: CoroutineDispatcher = Dispatchers.IO
    override val ui: CoroutineDispatcher = Dispatchers.Swing
}