package ru.moevm.moevm_checker.ui.courses_tree_content

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBScrollPane
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.BaseTreeView
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CourseTreeContextMenuActionListener
import javax.swing.JTree

class CoursesTreeView(
    private val component: PluginComponent,
): BaseView() {

    override val viewModel: CoursesTreeViewModel by simpleLazy {
        component.coursesTreeViewModel
    }

    private val contextMenuActionListener: CourseTreeContextMenuActionListener =
        object : CourseTreeContextMenuActionListener {
            override fun openTask(courseId: String, taskId: String) {
                viewModel.onOpenTaskClick(courseId, taskId)
            }

            override fun downloadTaskFiles(courseId: String, taskId: String) {
                viewModel.onDownloadTaskClick(courseId, taskId)
            }

            override fun removeTaskFiles(courseId: String, taskId: String) {
                viewModel.onRemoveTaskClick(courseId, taskId)
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
    }

    private fun createDialogPanel(): DialogPanel {
        val verticalFlowLayout = VerticalFlowLayout(/* fillHorizontally = */ true, /* fillVertically = */ true)
        val component = JBScrollPane(BaseTreeView(viewModel.coursesTreeModel, contextMenuActionListener).apply {
            coursesTree = this
            isVisible = true
        })

        return DialogPanel(verticalFlowLayout).apply {
            add(component)
        }
    }
}