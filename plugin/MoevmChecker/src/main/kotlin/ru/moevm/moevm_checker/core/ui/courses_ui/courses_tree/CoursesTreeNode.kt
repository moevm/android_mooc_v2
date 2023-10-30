package ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree

import ru.moevm.moevm_checker.core.ui.courses_ui.data.CourseVO
import ru.moevm.moevm_checker.core.ui.courses_ui.data.TaskVO
import ru.moevm.moevm_checker.utils.ResStr
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

class CoursesTreeNode(
    private val id: String,
    val title: String,
    private val type: String,
    private val isLeaf: Boolean
) : DefaultMutableTreeNode() {

    init {
        allowsChildren = !isLeaf
    }

    override fun toString(): String {
        return title
    }

    override fun getChildAt(childIndex: Int): TreeNode? {
        return children?.get(childIndex)
    }

    override fun getChildCount(): Int {
        return children?.size ?: 0
    }

    override fun getParent(): TreeNode? {
        return parent
    }

    override fun getIndex(node: TreeNode?): Int {
        return children?.indexOf(node as CoursesTreeNode) ?: -1
    }

    override fun getAllowsChildren(): Boolean {
        return !isLeaf
    }

    override fun isLeaf(): Boolean {
        return isLeaf
    }

    override fun children(): Enumeration<TreeNode> {
        return Collections.enumeration(children)
    }

    companion object {
        fun buildTreeWithNodes(treeItems: List<CourseVO>): CoursesTreeNode {
            val root = CoursesTreeNode(
                "-1",
                ResStr.getString("CoursesTreeCoursesHub"),
                "root",
                treeItems.isEmpty()
            )
            val children = treeItems.map { courseVO -> fromCourseVO(courseVO, root) }
            return root.apply {
                this.parent = null
                this.children = Vector(children)
            }
        }

        private fun fromCourseVO(courseVO: CourseVO, parent: CoursesTreeNode?): CoursesTreeNode {
            val result = CoursesTreeNode(
                courseVO.courseId,
                courseVO.courseName,
                "course", // FIXME: Заменить на enum
                courseVO.courseTasks.isEmpty()
            )
            val children = courseVO.courseTasks.map { taskVO -> fromTaskVO(taskVO, result) }
            return result.apply {
                this.parent = parent
                this.children = Vector(children)
            }
        }


        private fun fromTaskVO(taskVO: TaskVO, parent: CoursesTreeNode?): CoursesTreeNode {
            return CoursesTreeNode(
                taskVO.taskId,
                taskVO.taskName,
                taskVO.taskType,
                true
            ).apply {
                this.parent = parent
            }
        }
    }
}
