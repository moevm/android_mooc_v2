package ru.moevm.moevm_checker.ui.courses_tree_content

import com.intellij.collaboration.ui.SimpleHtmlPane
import com.intellij.collaboration.ui.setHtmlBody
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.BaseTreeView
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CourseTreeContextMenuActionListener
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.CoursesTreeCellRender
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
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

    private val treeSelectionListener = TreeSelectionListener { event ->
        viewModel.onCourseTreeNodeChanged(event.newLeadSelectionPath.lastPathComponent as DefaultMutableTreeNode?)
    }

    /*  UI Components   */
    private lateinit var coursesTree: JTree
    private lateinit var htmlPreview: JEditorPane
    private lateinit var loadingPreview: JLabel


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

        viewModel.taskDescription
            .onEach { description ->
                htmlPreview.setHtmlBody(description?.let { convertMarkdownToHtml(description) } ?: "")
            }
            .launchIn(viewScope)
        viewModel.isDescriptionLoading
            .onEach { isLoading ->
                htmlPreview.isVisible = !isLoading
                loadingPreview.isVisible = isLoading
            }.launchIn(viewScope)
    }

    private fun createDialogPanel(): DialogPanel {
        val component = JBScrollPane(BaseTreeView(viewModel.coursesTreeModel, contextMenuActionListener).apply {
            cellRenderer = CoursesTreeCellRender()
            coursesTree = this
            coursesTree.apply {
                showsRootHandles = false
                addTreeSelectionListener(treeSelectionListener)
            }
            isVisible = true
        }).apply {
            preferredSize = preferredSize.apply { height = 400 }
        }

        return panel {
            row {
                cell(component).align(Align.FILL)
            }.bottomGap(BottomGap.SMALL)
            group(title = "Preview") {
                row {
                    cell(SimpleHtmlPane("").apply {
                        htmlPreview = this
                        isEditable = false
                        visible(true)
                    })
                    icon(AnimatedIcon.Default()).applyToComponent {
                        loadingPreview = this
                        visible(false)
                    }
                }
            }
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

    private fun convertMarkdownToHtml(markdown: String): String {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
        val html = HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
        return html
    }

    override fun destroy() {
        coursesTree.removeTreeSelectionListener(treeSelectionListener)
        super.destroy()
    }
}