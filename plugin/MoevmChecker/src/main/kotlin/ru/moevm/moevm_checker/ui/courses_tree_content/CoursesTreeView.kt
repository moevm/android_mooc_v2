package ru.moevm.moevm_checker.ui.courses_tree_content

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.BaseTreeView
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CourseTreeContextMenuActionListener
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CoursesTreeCellRender
import javax.swing.JTree
import javax.swing.tree.TreePath

class CoursesTreeView(
    private val component: PluginComponent,
): BaseView() {

    override val viewModel: CoursesTreeViewModel by simpleLazy {
        component.coursesTreeViewModel
    }

    private val contextMenuActionListener: CourseTreeContextMenuActionListener =
        object : CourseTreeContextMenuActionListener {
            override fun openTask(taskReference: TaskReference) {
                viewModel.onOpenTaskClick(taskReference)
            }

            override fun downloadTaskFiles(taskReference: TaskReference) {
                viewModel.onDownloadTaskClick(taskReference)
            }

            override fun removeTaskFiles(taskReference: TaskReference) {
                viewModel.onRemoveTaskClick(taskReference)
            }
        }

    /*  UI Components   */
    private lateinit var coursesTree: JTree

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "Courses overview"
        val dialogPanel = createDialogPanel()
        bindEvents()
        viewModel.onViewCreated()
        return DialogPanelData(panelName, dialogPanel)
    }

    private fun bindEvents() {
        viewModel.shouldTreeInvalidate
            .onEach {
                coursesTree.invalidate()
            }
            .launchIn(viewScope)

        viewModel.shouldTreeRepaint
            .onEach { listOfTaskPath ->
                repaintInProgressNodes(listOfTaskPath)
            }
            .launchIn(viewScope)
    }

    private fun createDialogPanel(): DialogPanel {
        val verticalFlowLayout = VerticalFlowLayout(/* fillHorizontally = */ true, /* fillVertically = */ true)
        val component = JBScrollPane(BaseTreeView(viewModel.coursesTreeModel, contextMenuActionListener).apply {
            cellRenderer = CoursesTreeCellRender()
            coursesTree = this
            isVisible = true
        })

        return panel {
            row {
                cell(component).align(Align.FILL)
            }

        }.apply {
            layout = verticalFlowLayout
        }
    }

    private fun repaintInProgressNodes(listOfTaskPath: List<TreePath>) {
        listOfTaskPath.forEach { path ->
            val bounds = coursesTree.getPathBounds(path)
            if (bounds != null) {
                coursesTree.repaint(bounds)
            }
        }
    }
}