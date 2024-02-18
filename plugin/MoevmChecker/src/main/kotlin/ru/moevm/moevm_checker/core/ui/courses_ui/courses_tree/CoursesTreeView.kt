package ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree

import com.intellij.ide.projectView.impl.ProjectViewTree
import ru.moevm.moevm_checker.utils.ResStr
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.SwingUtilities
import javax.swing.tree.TreeModel

class CoursesTreeView(
    treeModel: TreeModel,
    private val contextMenuActionListener: CourseTreeContextMenuActionListener
) : ProjectViewTree(treeModel) {
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

                val itemByPoint = getTreeNodeByPoint(e.point)
                itemByPoint?.let {
                    showContextMenu(itemByPoint, e.point)
                }
            }

            override fun mouseReleased(e: MouseEvent?) = Unit

            override fun mouseEntered(e: MouseEvent?) = Unit

            override fun mouseExited(e: MouseEvent?) = Unit

        })
    }

    private fun showContextMenu(node: CoursesTreeNode, point: Point) {
        val popupMenuItems = buildList {
            if (isItemOpenable(node.coursesItemType)) {
                add(PopupAction(ResStr.getString("CoursesTreePopupMenuOpenTask")) {
                    contextMenuActionListener.openTask(
                        node.nodeId
                    )
                })
            }
            if (isItemDownloadable(node.coursesItemType)) {
                add(PopupAction(ResStr.getString("CoursesTreePopupMenuDownloadTask")) {
                    contextMenuActionListener.downloadTaskFiles(
                        node.nodeId
                    )
                })
            }
            if (isItemRemovable(node.coursesItemType)) {
                add(PopupAction(ResStr.getString("CoursesTreePopupMenuRemoveTaskFiles")) {
                    contextMenuActionListener.removeTaskFiles(
                        node.nodeId
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

    private fun getTreeNodeByPoint(point: Point): CoursesTreeNode? {
        val row = getRowForLocation(point.x, point.y)
        if (row == -1) {
            return null
        }
        val pathToRow = getPathForRow(row)
        val selectedNode = pathToRow.lastPathComponent
        if (selectedNode is CoursesTreeNode) {
            return selectedNode
        }
        return null
    }
}