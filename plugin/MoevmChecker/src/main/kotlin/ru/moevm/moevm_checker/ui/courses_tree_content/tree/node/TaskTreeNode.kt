package ru.moevm.moevm_checker.ui.courses_tree_content.tree.node

import ru.moevm.moevm_checker.core.tasks.TaskFileStatus
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesItemType
import javax.swing.tree.DefaultMutableTreeNode

class TaskTreeNode(
    val taskReference: TaskReference,
    val title: String,
    val taskType: CoursesItemType,
    var fileStatus: TaskFileStatus,
) : DefaultMutableTreeNode(/* userObject = */ null, /* allowsChildren = */ false) {

    override fun toString(): String {
        return title
    }
}