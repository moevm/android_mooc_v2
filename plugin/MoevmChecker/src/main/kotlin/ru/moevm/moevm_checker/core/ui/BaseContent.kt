package ru.moevm.moevm_checker.core.ui

import ru.moevm.moevm_checker.core.ui.data.DialogPanelData

interface BaseContent {
    fun getDialogPanel(): DialogPanelData
}