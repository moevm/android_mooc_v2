package ru.moevm.moevm_checker.ui.task.android

import com.intellij.ui.AnimatedIcon
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.tasks.TaskResultCodeEncoder
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData
import ru.moevm.moevm_checker.ui.HtmlTextPreviewPanel
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.text.JTextComponent

class AndroidTaskView(
    private val component: PluginComponent,
    private val taskReference: TaskReference
) : BaseView() {

    /*  UI Components   */
    private lateinit var taskDescriptionPreview: HtmlTextPreviewPanel
    private lateinit var loadingProblemSpinner: JLabel
    private lateinit var textResult: Cell<JEditorPane>
    private lateinit var textTaskResultCode: Cell<JBTextArea>
    private lateinit var textStdout: Cell<JBTextArea>
    private lateinit var textStderr: Cell<JBTextArea>
    private lateinit var testing: Row
    private lateinit var mainPanel: JPanel

    override val viewModel: AndroidTaskViewModel by simpleLazy {
        component.androidTaskViewModel
    }

    private val mainPanelComponentListener = object : ComponentListener {
        override fun componentResized(e: ComponentEvent?) {
            val width = e?.component?.width ?: return
            taskDescriptionPreview.updateSize(width)
        }

        override fun componentMoved(e: ComponentEvent?) {}

        override fun componentShown(e: ComponentEvent?) {
            val width = e?.component?.width ?: return
            taskDescriptionPreview.updateSize(width)
        }

        override fun componentHidden(e: ComponentEvent?) {}
    }


    override fun getDialogPanel(): DialogPanelData {
        val panelName = "MOEVM Checker"
        val dialogPanel = createScrollableDialogPanel()
        bindEvents()
        viewModel.onViewCreated(taskReference)
        return DialogPanelData(panelName, dialogPanel)
    }

    private fun bindEvents() {
        viewModel.taskDescription
            .onEach { description ->
                taskDescriptionPreview.updateText(convertMarkdownToHtml(description ?: ""))
            }
            .launchIn(viewScope)
        viewModel.isDescriptionLoading
            .onEach { isLoading ->
                loadingProblemSpinner.isVisible = isLoading
                taskDescriptionPreview.isVisible = !isLoading
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
                if (it?.taskResultCode != null) {
                    textTaskResultCode.showAndSetOrHideAndClearText(
                        "${it.taskResultCode}, ${
                            TaskResultCodeEncoder().decode(
                                it.taskResultCode
                            )
                        }"
                    )
                }
                textStdout.showAndSetOrHideAndClearText(it?.stdout)
                textStderr.showAndSetOrHideAndClearText(it?.stderr)
            }
            .launchIn(viewScope)
    }

    private fun createScrollableDialogPanel() = panel {
        row {
            cell(
                JBScrollPane(
                    createDialogPanel(),
                    JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                )
            )
                .align(Align.FILL)
        }.resizableRow()
    }

    private fun createDialogPanel() = panel {
        group(title = "Задача", indent = false) {
            row {
                cell(HtmlTextPreviewPanel()).applyToComponent {
                    taskDescriptionPreview = this
                    visible(false)
                    preferredSize = preferredSize.apply {
                        height = 400
                    }
                }
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

        group(title = "Результат") {
            testing = row {
                icon(AnimatedIcon.Default())
                label("Проверка решения...")
            }.apply { visible(false) }

            row {
                textResult = text("")
                    .label("Результат: ")
                    .visible(false)
            }
            row {
                textTaskResultCode = textArea()
                    .label("Итоговый код проверки: ", position = LabelPosition.TOP)
                    .align(AlignX.FILL)
                    .visible(false)
            }
            row {
                textStdout = textArea()
                    .label("Поток вывода (stdout): ", position = LabelPosition.TOP)
                    .align(AlignX.FILL)
                    .visible(false)
            }
            row {
                textStderr = textArea()
                    .label("Поток ошибок (stderr): ", position = LabelPosition.TOP)
                    .align(AlignX.FILL)
                    .visible(false)
            }
        }
    }.apply {
        mainPanel = this
        addComponentListener(mainPanelComponentListener)
    }

    override fun destroy() {
        mainPanel.removeComponentListener(mainPanelComponentListener)
        super.destroy()
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