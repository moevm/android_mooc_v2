package ru.moevm.moevm_checker.ui.courses_tree_content.tree.node

import javax.swing.tree.DefaultMutableTreeNode

class CoursesTreeNode(
    private val courseId: String,
    private val title: String,
): DefaultMutableTreeNode(/* userObject = */ null, /* allowsChildren = */ true) {

    override fun toString(): String {
        return title
    }
}