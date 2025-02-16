package ru.moevm.moevm_checker.ui.courses_tree_content.tree

import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class CoursesTreeViewNodeMenu(
    popupActions: List<PopupAction>
): JPopupMenu() {
    private val menuItems = popupActions.map { (title, action) ->
        JMenuItem(title).apply {
            addActionListener {
                action()
            }
        }
    }

    init {
        menuItems.forEach { item ->
            add(item)
        }
    }
}