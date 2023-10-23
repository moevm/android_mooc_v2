package ru.mse.moevm_checker.core.ui.courses_ui.courses_tree

import ru.mse.moevm_checker.utils.ResStr
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class CoursesTreeViewNodeMenu(
    taskType: String, // TODO: Заменить на Enum
    onOpenTaskClick: () -> Unit,
    onDownloadTaskClick: () -> Unit,
    onRemoveTaskFilesClick: () -> Unit
) : JPopupMenu() {
    private val openTask = JMenuItem(ResStr.getString("CoursesTreePopupMenuOpenTask"))
    private val downloadTask = JMenuItem(ResStr.getString("CoursesTreePopupMenuDownloadTask"))
    private val removeTaskFiles = JMenuItem(ResStr.getString("CoursesTreePopupMenuRemoveTaskFiles"))

    init {
        openTask.addActionListener {
            onOpenTaskClick()
        }
        downloadTask.addActionListener {
            onDownloadTaskClick()
        }
        removeTaskFiles.addActionListener {
            onRemoveTaskFilesClick()
        }
        add(openTask)
        add(downloadTask)
        add(removeTaskFiles)
    }
}
