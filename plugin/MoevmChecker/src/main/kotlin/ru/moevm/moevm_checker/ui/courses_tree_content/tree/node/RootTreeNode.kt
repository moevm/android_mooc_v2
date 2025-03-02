package ru.moevm.moevm_checker.ui.courses_tree_content.tree.node

import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.data.TaskVO
import javax.swing.tree.DefaultMutableTreeNode

class RootTreeNode private constructor(
    private val nodeId: String,
    private val title: String,
) : DefaultMutableTreeNode(/* userObject = */ null, /* allowsChildren = */ true) {

    init {
        setParent(null)
    }

    override fun toString(): String {
        return title
    }

    companion object {

        fun buildEmptyTree() = RootTreeNode(
            "-1",
            "Каталог курсов",
        )

        fun buildTreeWithNodes(treeItems: List<CourseVO>): RootTreeNode {
            val root = RootTreeNode(
                "-1",
                "Каталог курсов",
            )
            val children = treeItems.map { courseVO -> fromCourseVO(courseVO) }
            children.forEach { coursesTreeNode ->
                root.add(coursesTreeNode)
            }
            return root
        }

        private fun fromCourseVO(courseVO: CourseVO): CoursesTreeNode {
            val course = CoursesTreeNode(
                courseVO.id,
                courseVO.name,
            )
            val children = courseVO.tasks.map { taskVO -> fromTaskVO(courseVO, taskVO) }
            children.forEach { taskTreeNode ->
                course.add(taskTreeNode)
            }
            return course
        }

        private fun fromTaskVO(courseVO: CourseVO, taskVO: TaskVO): TaskTreeNode {
            return TaskTreeNode(
                courseVO.id,
                taskVO.taskId,
                taskVO.name,
                taskVO.type,
            )
        }
    }
}