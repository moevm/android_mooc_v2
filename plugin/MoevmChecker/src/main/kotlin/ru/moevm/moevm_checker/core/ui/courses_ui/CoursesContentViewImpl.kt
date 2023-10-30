package ru.moevm.moevm_checker.core.ui.courses_ui

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBScrollPane
import ru.moevm.moevm_checker.core.ui.BaseContent
import ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree.CoursesTreeModel
import ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree.CoursesTreeNode
import ru.moevm.moevm_checker.core.ui.courses_ui.courses_tree.CoursesTreeView
import ru.moevm.moevm_checker.core.ui.courses_ui.data.CourseVO
import ru.moevm.moevm_checker.core.ui.data.DialogPanelData
import javax.swing.JTree

class CoursesContentViewImpl(
    projectPath: String
) : BaseContent, CoursesContentView {
    override val presenter = CoursesPresenterImpl(this)
    private val coursesTreeModel = CoursesTreeModel(
        CoursesTreeNode.buildTreeWithNodes(emptyList())
    )

    /*  UI Components   */
    private lateinit var coursesTree: JTree

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "Courses overview"
        val dialogPanel = createDialogPanel()
        refreshUiState(emptyList())
        presenter.onCoursesContentViewCreated()
        return DialogPanelData(panelName, dialogPanel)
    }

    private fun createDialogPanel(): DialogPanel {
        val verticalFlowLayout = VerticalFlowLayout(/* fillHorizontally = */ true, /* fillVertically = */ true)
        val component = JBScrollPane(
            CoursesTreeView(coursesTreeModel).apply {
                coursesTree = this
                isVisible = true
            }
        )
        return DialogPanel(verticalFlowLayout).apply {
            add(component)
        }
    }

    override fun refreshUiState(courses: List<CourseVO>) {
        coursesTreeModel.updateRoot(courses)
        coursesTree.invalidate()
    }
}