package ru.mse.moevm_checker.core.ui.courses_ui.courses_tree

import ru.mse.moevm_checker.core.ui.courses_ui.data.CourseVO
import javax.swing.tree.DefaultTreeModel

class CoursesTreeModel(
    root: CoursesTreeNode
) : DefaultTreeModel(root) {

    fun updateRoot(courses: List<CourseVO>) {
        val newNodes = CoursesTreeNode.buildTreeWithNodes(courses)
        this.setRoot(newNodes)
    }
}