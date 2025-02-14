package ru.moevm.moevm_checker.ui

import kotlinx.coroutines.CoroutineScope
import ru.moevm.moevm_checker.core.utils.Destroyable

abstract class BaseView : Destroyable {

    protected abstract val viewModel: BaseViewModel
    protected val viewScope: CoroutineScope
        get() = viewModel.viewModelScope

    override fun destroy() {
        viewModel.destroy()
        println("${this::class.java.simpleName} destroyed")
    }

    abstract fun getDialogPanel(): DialogPanelData
}