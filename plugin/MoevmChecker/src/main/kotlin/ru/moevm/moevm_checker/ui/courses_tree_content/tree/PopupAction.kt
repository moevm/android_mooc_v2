package ru.moevm.moevm_checker.ui.courses_tree_content.tree

data class PopupAction(
    val actionTitle: String,
    val action: () -> Unit
)
