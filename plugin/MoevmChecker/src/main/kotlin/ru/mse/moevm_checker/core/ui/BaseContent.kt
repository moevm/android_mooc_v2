package ru.mse.moevm_checker.core.ui

import ru.mse.moevm_checker.core.ui.data.DialogPanelData

interface BaseContent {
    fun getDialogPanel(): DialogPanelData
}