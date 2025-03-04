package ru.moevm.moevm_checker.ui.courses_tree_content.tree.node

import javax.swing.tree.DefaultMutableTreeNode

class CoursesTreeNode(
    val courseId: String,
    val title: String,
): DefaultMutableTreeNode(/* userObject = */ null, /* allowsChildren = */ true) {

    override fun toString(): String {
        return title
    }
}