package ru.moevm.moevm_checker.ui.courses_tree_content.tree

import com.intellij.ide.projectView.impl.ProjectViewTree
import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesItemType
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.TaskTreeNode
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.SwingUtilities
import javax.swing.tree.TreeModel

class BaseTreeView(
    treeModel: TreeModel,
    private val contextMenuActionListener: CourseTreeContextMenuActionListener
): ProjectViewTree(treeModel) {

    init {
        setMouseRightClickOnItem(this)
    }

    private fun setMouseRightClickOnItem(projectViewTree: ProjectViewTree) {
        projectViewTree.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) = Unit

            override fun mousePressed(e: MouseEvent) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return
                }

                val itemByPoint = getTaskTreeNodeByPoint(e.point)
                itemByPoint?.let {
                    showContextMenu(itemByPoint, e.point)
                }
            }

            override fun mouseReleased(e: MouseEvent?) = Unit

            override fun mouseEntered(e: MouseEvent?) = Unit

            override fun mouseExited(e: MouseEvent?) = Unit
        })
    }

    private fun showContextMenu(node: TaskTreeNode, point: Point) {
        val popupMenuItems = buildList {
            if (isItemOpenable(node.taskType)) {
                add(PopupAction("Открыть задачу") {
                    contextMenuActionListener.openTask(
                        node.courseId,
                        node.taskId,
                    )
                })
            }
            if (isItemDownloadable(node.taskType)) {
                add(PopupAction("Загрузить задачу") {
                    contextMenuActionListener.downloadTaskFiles(
                        node.courseId,
                        node.taskId,
                    )
                })
            }
            if (isItemRemovable(node.taskType)) {
                add(PopupAction("Удалить файлы задачи") {
                    contextMenuActionListener.removeTaskFiles(
                        node.courseId,
                        node.taskId,
                    )
                })
            }
        }

        val popupMenu = CoursesTreeViewNodeMenu(popupMenuItems)
        popupMenu.show(this, point.x, point.y)
    }

    private fun isItemOpenable(coursesItemType: CoursesItemType) = when (coursesItemType) {
        CoursesItemType.CODE_TASK -> true
        else -> false
    }

    private fun isItemDownloadable(coursesItemType: CoursesItemType) = when (coursesItemType) {
        CoursesItemType.CODE_TASK -> true
        else -> false
    }

    private fun isItemRemovable(coursesItemType: CoursesItemType) = when (coursesItemType) {
        CoursesItemType.CODE_TASK -> true
        else -> false
    }

    private fun getTaskTreeNodeByPoint(point: Point): TaskTreeNode? {
        val row = getRowForLocation(point.x, point.y)
        if (row == -1) {
            return null
        }
        val pathToRow = getPathForRow(row)
        val selectedNode = pathToRow.lastPathComponent
        if (selectedNode is TaskTreeNode) {
            return selectedNode
        }
        return null
    }
}