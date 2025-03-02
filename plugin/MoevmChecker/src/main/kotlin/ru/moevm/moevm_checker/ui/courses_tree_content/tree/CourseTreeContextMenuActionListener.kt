package ru.moevm.moevm_checker.ui.courses_tree_content.tree

interface CourseTreeContextMenuActionListener {

    fun openTask(courseId: String, taskId: String)

    fun downloadTaskFiles(courseId: String, taskId: String)

    fun removeTaskFiles(courseId: String, taskId: String)
}