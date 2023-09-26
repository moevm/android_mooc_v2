package ru.mse.moevm_checker.core.ui

import com.intellij.openapi.ui.DialogPanel

interface BaseWindow {
    fun getContent(): DialogPanel
}