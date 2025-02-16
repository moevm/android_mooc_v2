package ru.moevm.moevm_checker.ui.courses_tree_content

import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CoursesTreeNode
import javax.swing.tree.DefaultTreeModel

class CoursesTreeModel(
    root: CoursesTreeNode
): DefaultTreeModel(root) {

    fun updateTree(courses: List<CourseVO>) {
        val newNodes = CoursesTreeNode.buildTreeWithNodes(courses)
        setRoot(newNodes)
    }
}