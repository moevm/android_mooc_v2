package ru.moevm.moevm_checker.ui.task.android

import com.android.tools.idea.appinspection.inspectors.network.view.details.createVerticalScrollPane
import com.intellij.collaboration.ui.SimpleHtmlPane
import com.intellij.collaboration.ui.setHtmlBody
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.JBEmptyBorder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.text.JTextComponent

class AndroidTaskView(
    private val component: PluginComponent,
    private val courseId: String,
    private val taskId: String,
) : BaseView() {

    /*  UI Components   */
    private lateinit var htmlTaskProblemEditor: JEditorPane
    private lateinit var loadingProblemSpinner: JLabel
    private lateinit var textResult: Cell<JEditorPane>
    private lateinit var textStdout: Cell<JBTextArea>
    private lateinit var textStderr: Cell<JBTextArea>
    private lateinit var testing: Row

    override val viewModel: AndroidTaskViewModel by simpleLazy {
        component.androidTaskViewModel
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
                htmlTaskProblemEditor.setHtmlBody(description?.let { convertMarkdownToHtml(description) } ?: "")
            }
            .launchIn(viewScope)
        viewModel.isDescriptionLoading
            .onEach { isLoading ->
                loadingProblemSpinner.isVisible = isLoading
                htmlTaskProblemEditor.isVisible = !isLoading
            }
            .launchIn(viewScope)

        viewModel.isTestInProgress
            .onEach { inProgress ->
                testing.visible(inProgress)
            }
            .launchIn(viewScope)

        viewModel.taskResultData
            .onEach {
                textResult.apply {
                    showAndSetOrHideAndClearText(it?.result?.toString())
                    if (it?.result != null) {
                        addColorByResult(it.result)
                    }
                }
                textStdout.showAndSetOrHideAndClearText(it?.stdout)
                textStderr.showAndSetOrHideAndClearText(it?.stderr)
            }
            .launchIn(viewScope)
    }

    private fun createScrollableDialogPanel() = panel {
        row {
            cell(createVerticalScrollPane(createDialogPanel()))
                .align(Align.FILL)
        }.resizableRow()
    }

    private fun createDialogPanel() = panel {
        group(title = "Problem") {
            row {
                cell(SimpleHtmlPane("").apply {
                    htmlTaskProblemEditor = this
                    isEditable = false
                    visible(false)
                })
                icon(AnimatedIcon.Default()).applyToComponent {
                    loadingProblemSpinner = this
                    visible(true)
                }
            }
            row {
                button("Проверить") {
                    viewModel.onTestClick()
                }
            }
        }

        group(title = "Result") {
            testing = row {
                icon(AnimatedIcon.Default())
                label("Проверка решения...")
            }.apply { visible(false) }

            row {
                textResult = text("")
                    .label("Result: ")
                    .visible(false)
            }
            row {
                textStdout = textArea()
                    .label("Stdout: ", position = LabelPosition.TOP)
                    .align(AlignX.FILL)
                    .visible(false)
            }
            row {
                textStderr = textArea()
                    .label("Stderr: ", position = LabelPosition.TOP)
                    .align(AlignX.FILL)
                    .visible(false)
            }
        }
    }.apply {
        border = JBEmptyBorder(12)
    }

    private fun Cell<JTextComponent>.showAndSetOrHideAndClearText(text: String?) {
        if (text == null) {
            visible(false)
            text("")
        } else {
            text(text)
            visible(true)
        }
    }
    private fun Cell<JTextComponent>.addColorByResult(result: CheckResult) {
        when (result) {
            CheckResult.Passed -> {
                this.component.foreground = JBColor.GREEN
            }
            else -> {
                this.component.foreground = JBColor.RED
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