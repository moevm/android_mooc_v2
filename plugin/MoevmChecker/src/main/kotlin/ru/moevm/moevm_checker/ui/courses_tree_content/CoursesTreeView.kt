package ru.moevm.moevm_checker.ui.courses_tree_content

import com.intellij.markdown.utils.convertMarkdownToHtml
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import ru.moevm.moevm_checker.ui.HtmlTextPreviewPanel
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.BaseTreeView
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CourseTreeContextMenuActionListener
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CoursesTreeCellRender
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

private const val COURSES_TREE_HEIGHT = 400

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

    private val treeSelectionListener = TreeSelectionListener { event ->
        viewModel.onCourseTreeNodeChanged(event.newLeadSelectionPath.lastPathComponent as DefaultMutableTreeNode?)
    }

    private val mainPanelComponentListener = object : ComponentListener {
        override fun componentResized(e: ComponentEvent?) {
            val width = e?.component?.width ?: return
            descriptionPreview.updateSize(width)
        }

        override fun componentMoved(e: ComponentEvent?) {}

        override fun componentShown(e: ComponentEvent?) {
            val width = e?.component?.width ?: return
            descriptionPreview.updateSize(width)
        }

        override fun componentHidden(e: ComponentEvent?) {}
    }

    /*  UI Components   */
    private lateinit var coursesTree: JTree
    private lateinit var descriptionPreview: HtmlTextPreviewPanel
    private lateinit var loadingPreviewSpinner: JLabel
    private lateinit var mainPanel: JPanel


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

        viewModel.descriptionState
            .onEach { description ->
                descriptionPreview.updateText(convertMarkdownToHtml(description ?: ""))
            }
            .launchIn(viewScope)
        viewModel.isDescriptionLoading
            .onEach { isLoading ->
                descriptionPreview.isVisible = !isLoading
                loadingPreviewSpinner.isVisible = isLoading
            }.launchIn(viewScope)
    }

    private fun createDialogPanel(): DialogPanel {
        val component = JBScrollPane(
            BaseTreeView(viewModel.coursesTreeModel, contextMenuActionListener).apply {
                cellRenderer = CoursesTreeCellRender()
                coursesTree = this
                coursesTree.apply {
                    showsRootHandles = false
                    addTreeSelectionListener(treeSelectionListener)
                }
                isVisible = true
            },
            JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        ).apply {
            preferredSize = preferredSize.apply { height = COURSES_TREE_HEIGHT }
        }

        return panel {
            row {
                cell(component).align(Align.FILL)
            }.bottomGap(BottomGap.SMALL)
            group(title = "Preview", indent = false) {
                row {
                    cell(HtmlTextPreviewPanel())
                        .applyToComponent {
                            descriptionPreview = this
                            visible(true)
                        }
                        .align(Align.FILL)
                    icon(AnimatedIcon.Default()).applyToComponent {
                        loadingPreviewSpinner = this
                        visible(false)
                    }.align(Align.FILL)
                }.resizableRow()
            }.resizableRow()
        }.apply {
            mainPanel = this
            addComponentListener(mainPanelComponentListener)
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

    override fun destroy() {
        coursesTree.removeTreeSelectionListener(treeSelectionListener)
        mainPanel.removeComponentListener(mainPanelComponentListener)
        super.destroy()
    }
}