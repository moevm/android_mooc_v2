package ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree

data class PopupAction(
    val actionTitle: String,
    val action: () -> Unit
)