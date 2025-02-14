package ru.moevm.moevm_checker.ui.courses_tree_content.tree

import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesItemType
import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.data.TaskVO
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

class CoursesTreeNode(
    val nodeId: String,
    private val title: String,
    val coursesItemType: CoursesItemType,
    private val isLeaf: Boolean
): DefaultMutableTreeNode() {

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
                "Каталог курсов",
                CoursesItemType.UNKNOWN,
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
                courseVO.id,
                courseVO.name,
                CoursesItemType.COURSE,
                courseVO.tasks.isEmpty()
            )
            val children = courseVO.tasks.map { taskVO -> fromTaskVO(taskVO, result) }
            return result.apply {
                this.parent = parent
                this.children = Vector(children)
            }
        }

        private fun fromTaskVO(taskVO: TaskVO, parent: CoursesTreeNode?): CoursesTreeNode {
            return CoursesTreeNode(
                taskVO.id,
                taskVO.name,
                getCoursesItemType(taskVO.type),
                true
            ).apply {
                this.parent = parent
            }
        }

        private fun getCoursesItemType(taskType: String): CoursesItemType {
            return when (taskType) {
                CoursesItemType.COURSE.type -> CoursesItemType.COURSE
                CoursesItemType.CODE_TASK.type -> CoursesItemType.CODE_TASK
                else -> CoursesItemType.UNKNOWN
            }
        }
    }
}