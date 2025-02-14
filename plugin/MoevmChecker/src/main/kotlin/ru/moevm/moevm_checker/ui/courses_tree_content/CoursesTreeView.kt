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
import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.BaseTreeView
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CourseTreeContextMenuActionListener
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CoursesTreeNode
import javax.swing.JTree

class CoursesTreeView(
    private val component: PluginComponent,
): BaseView() {

    override val viewModel: CoursesTreeViewModel by simpleLazy {
        component.coursesTreeViewModel
    }

    private val coursesTreeModel = CoursesTreeModel(
        CoursesTreeNode.buildTreeWithNodes(emptyList())
    )

    private val contextMenuActionListener: CourseTreeContextMenuActionListener =
        object : CourseTreeContextMenuActionListener {
            override fun openTask(id: String) {
//                viewModel.onOpenTaskClick(id)
            }

            override fun downloadTaskFiles(id: String) {
                viewModel.onDownloadTaskClick(id)
            }

            override fun removeTaskFiles(id: String) {
                viewModel.onRemoveTaskClick(id)
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
        viewModel.listOfCoursesState
            .onEach { newCourses ->
                refreshTree(newCourses)
            }
            .launchIn(viewScope)
    }

    private fun createDialogPanel(): DialogPanel {
        val verticalFlowLayout = VerticalFlowLayout(/* fillHorizontally = */ true, /* fillVertically = */ true)
        val component = JBScrollPane(BaseTreeView(coursesTreeModel, contextMenuActionListener).apply {
            coursesTree = this
            isVisible = true
        })

        return DialogPanel(verticalFlowLayout).apply {
            add(component)
        }
    }

    private fun refreshTree(courses: List<CourseVO>) {
        coursesTreeModel.updateTree(courses)
        coursesTree.invalidate()
    }
}