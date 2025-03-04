package ru.moevm.moevm_checker.ui.courses_tree_content

import com.intellij.ui.tree.TreePathUtil
import ru.moevm.moevm_checker.core.tasks.TaskFileStatus
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.CoursesTreeNode
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.RootTreeNode
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.TaskTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class CoursesTreeModel(
    initRoot: RootTreeNode,
) : DefaultTreeModel(initRoot) {

    fun updateTree(courses: List<CourseVO>) {
        val root = RootTreeNode.buildTreeWithNodes(courses)
        setRoot(root)
    }

    fun updateTaskFileStatus(taskReference: TaskReference, newFileStatus: TaskFileStatus) {
        val node = findNodeByTaskReference(taskReference) as TaskTreeNode?
        node?.fileStatus = newFileStatus
        if (node != null) {
            nodeChanged(node)
        }
    }

    fun getPathToNode(taskReference: TaskReference): TreePath {
        return TreePathUtil.convertArrayToTreePath(*getPathToRoot(findNodeByTaskReference(taskReference)))
    }

    private fun findNodeByTaskReference(taskReference: TaskReference): TreeNode? {
        val root = this.root ?: return null
        val course = root.children().asSequence().find { (it as CoursesTreeNode).courseId == taskReference.courseId }
        val task = (course as CoursesTreeNode).children().asSequence()
            .find { (it as TaskTreeNode).taskReference == taskReference }
        return task
    }
}