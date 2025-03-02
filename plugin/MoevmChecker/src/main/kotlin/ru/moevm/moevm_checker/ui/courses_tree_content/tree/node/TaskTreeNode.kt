package ru.moevm.moevm_checker.ui.courses_tree_content.tree.node

import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesItemType
import javax.swing.tree.DefaultMutableTreeNode

class TaskTreeNode(
    val courseId: String,
    val taskId: String,
    private val title: String,
    taskTypeStr: String,
) : DefaultMutableTreeNode(/* userObject = */ null, /* allowsChildren = */ false) {

    val taskType = getCoursesItemType(taskTypeStr)

    override fun toString(): String {
        return title
    }

    private companion object {
        private fun getCoursesItemType(taskType: String): CoursesItemType {
            return when (taskType) {
                CoursesItemType.CODE_TASK.type -> CoursesItemType.CODE_TASK
                else -> CoursesItemType.UNKNOWN
            }
        }
    }
}