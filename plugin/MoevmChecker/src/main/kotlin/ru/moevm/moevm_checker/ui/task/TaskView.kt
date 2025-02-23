package ru.moevm.moevm_checker.ui.task

import com.android.tools.idea.appinspection.inspectors.network.view.details.createVerticalScrollPane
import com.intellij.collaboration.ui.SimpleHtmlPane
import com.intellij.collaboration.ui.setHtmlBody
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import javax.swing.JEditorPane

class TaskView(
    private val component: PluginComponent,
    private val courseId: String,
    private val taskId: String,
) : BaseView() {

    /*  UI Components   */
    private lateinit var htmlTaskProblem: JEditorPane
    private lateinit var textResult: JEditorPane
    private lateinit var textStdout: JEditorPane
    private lateinit var textStderr: JEditorPane
    private lateinit var testing: Row

    override val viewModel: TaskViewModel by simpleLazy {
        component.taskViewModel
    }

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "MOEVM Checker"
        val dialogPanel = createScrollableDialogPanel()
        bindEvents()
        viewModel.onViewCreated(courseId, taskId)
        return DialogPanelData(panelName, dialogPanel)
    }

    private fun bindEvents() {
        viewModel.taskDescription
            .onEach { description ->
                htmlTaskProblem.setHtmlBody(description?.let { convertMarkdownToHtml(description) } ?: "")
            }
            .launchIn(viewScope)

        viewModel.isTestInProgress
            .onEach { inProgress ->
                testing.visible(inProgress)
            }
            .launchIn(viewScope)

        viewModel.taskResultData
            .onEach {  }
    }

    private fun createScrollableDialogPanel() = panel {
        row {
            cell(createVerticalScrollPane(createDialogPanel()))
                .align(Align.FILL)
        }.resizableRow()
    }

    private fun createDialogPanel() = panel {
        row {
            cell(SimpleHtmlPane("").apply {
                htmlTaskProblem = this
                visible(true)
                isEditable = false
            })
        }
        group {
            row {
                button("Проверить") {
                    viewModel.onTestClick()
                }
            }

            testing = row {
                icon(AnimatedIcon.Default())
                label("Проверка решения...")
            }.apply { visible(false) }

            row {
                textResult = text("").component
            }
            row {
                textStdout = text("").component
            }
            row {
                textStderr = text("").component
            }
        }
    }

    private fun convertMarkdownToHtml(markdown: String): String {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
        val html = HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
        return html
    }
}