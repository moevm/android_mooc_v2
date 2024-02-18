package ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree

interface CourseTreeContextMenuActionListener {

    fun openTask(id: String)

    fun downloadTaskFiles(id: String)

    fun removeTaskFiles(id: String)
}