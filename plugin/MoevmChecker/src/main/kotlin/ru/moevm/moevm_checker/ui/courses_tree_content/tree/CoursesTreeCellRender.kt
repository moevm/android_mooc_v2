package ru.moevm.moevm_checker.ui.courses_tree_content.tree

import ru.moevm.moevm_checker.core.tasks.TaskFileStatus
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.CoursesTreeNode
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.RootTreeNode
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.TaskTreeNode
import ru.moevm.moevm_checker.utils.Icons
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.tree.DefaultTreeCellRenderer

class CoursesTreeCellRender : DefaultTreeCellRenderer() {
    private val taskFileStatusToIcon = mapOf(
        TaskFileStatus.NOT_DOWNLOADABLE to null,
        TaskFileStatus.AVAILABLE to Icons.available,
        TaskFileStatus.DOWNLOADING to Icons.progress,
        TaskFileStatus.REMOVING to Icons.progress,
        TaskFileStatus.DOWNLOADED to Icons.downloaded,
        TaskFileStatus.OUTDATED to Icons.outdated,
    )

    override fun getTreeCellRendererComponent(
        tree: JTree?,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        val baseRenderer = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus) as JLabel
        when (value) {
            is RootTreeNode -> {
                baseRenderer.icon = Icons.rootNode
                baseRenderer.text = value.title
            }
            is CoursesTreeNode -> {
                baseRenderer.icon = Icons.course
                baseRenderer.text = value.title
            }
            is TaskTreeNode -> {
                baseRenderer.icon = taskFileStatusToIcon.getOrDefault(value.fileStatus, null)
                baseRenderer.text = value.title
            }
        }
        return baseRenderer
    }
}
