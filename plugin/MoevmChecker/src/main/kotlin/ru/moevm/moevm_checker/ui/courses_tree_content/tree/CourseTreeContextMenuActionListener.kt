package ru.moevm.moevm_checker.ui.courses_tree_content.tree

interface CourseTreeContextMenuActionListener {

    fun openTask(id: String)

    fun downloadTaskFiles(id: String)

    fun removeTaskFiles(id: String)
}