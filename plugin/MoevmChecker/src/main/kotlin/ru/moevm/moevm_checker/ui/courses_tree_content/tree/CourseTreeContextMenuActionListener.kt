package ru.moevm.moevm_checker.ui.courses_tree_content.tree

import ru.moevm.moevm_checker.core.tasks.TaskReference

interface CourseTreeContextMenuActionListener {

    fun openTask(taskReference: TaskReference)

    fun downloadTaskFiles(taskReference: TaskReference)

    fun removeTaskFiles(taskReference: TaskReference)
}