package ru.moevm.moevm_checker.ui

import kotlinx.coroutines.*
import ru.moevm.moevm_checker.core.utils.Destroyable

open class BaseViewModel(uiDispatcher: CoroutineDispatcher) : Destroyable {

    private val job = SupervisorJob()
    val viewModelScope: CoroutineScope = CoroutineScope(uiDispatcher + job)

    override fun destroy() {
        viewModelScope.cancel()
        println("${this::class.java.simpleName} destroyed")
    }
}