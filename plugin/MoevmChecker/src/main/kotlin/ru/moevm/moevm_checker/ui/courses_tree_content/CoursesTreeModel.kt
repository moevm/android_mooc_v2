package ru.moevm.moevm_checker.ui.courses_tree_content

import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.RootTreeNode
import javax.swing.tree.DefaultTreeModel

class CoursesTreeModel(
    root: RootTreeNode,
): DefaultTreeModel(root) {

    fun updateTree(courses: List<CourseVO>) {
        val root = RootTreeNode.buildTreeWithNodes(courses)
        setRoot(root)
    }
}