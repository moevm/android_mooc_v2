package ru.mse.moevm_checker.core.ui.courses_ui.courses_tree

import com.intellij.ide.projectView.impl.ProjectViewTree
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.SwingUtilities
import javax.swing.tree.TreeModel

class CoursesTreeView(
    treeModel: TreeModel
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
        val popupMenu = CoursesTreeViewNodeMenu("", {}, {}, {})
        popupMenu.show(this, point.x, point.y)
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