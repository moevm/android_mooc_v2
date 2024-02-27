package ru.moevm.moevm_checker.core.ui.task_ui

import com.android.tools.idea.appinspection.inspectors.network.view.details.createVerticalScrollPane
import com.intellij.collaboration.ui.SimpleHtmlPane
import com.intellij.collaboration.ui.setHtmlBody
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import ru.moevm.moevm_checker.core.ui.BaseContent
import ru.moevm.moevm_checker.core.ui.data.DialogPanelData
import ru.moevm.moevm_checker.utils.ResStr
import javax.swing.JEditorPane

class TaskViewImpl : BaseContent, TaskView {
    /*  UI Components   */
    private lateinit var htmlTaskProblem: JEditorPane
    private lateinit var textResult: JEditorPane
    private lateinit var textStdout: JEditorPane
    private lateinit var textStderr: JEditorPane
    private lateinit var testing: Row

    override val presenter = TaskPresenterImpl(this)

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "Task checker"
        val dialogPanel = createScrollableDialogPanel()
        presenter.onViewCreated()
        return DialogPanelData(panelName, dialogPanel)
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
                button(ResStr.getString("TaskCheckButtonText")) {
                    refreshUiState(
                        isLoadingLabelVisible = true,
                        htmlTaskProblemText = htmlTaskProblem.text,
                        taskResultText = "",
                        taskStdoutText = "",
                        taskStderrText = ""
                    )
                    presenter.onCheckClicked()
                }
            }

            testing = row {
                icon(AnimatedIcon.Default())
                label(ResStr.getString("TaskLoadingTitle"))
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

    override fun refreshUiState(
        isLoadingLabelVisible: Boolean,
        htmlTaskProblemText: String,
        taskResultText: String,
        taskStdoutText: String,
        taskStderrText: String
    ) {
        testing.visible(isLoadingLabelVisible)
        htmlTaskProblem.setHtmlBody(htmlTaskProblemText)
        textResult.text = taskResultText
        textStdout.text = taskStdoutText
        textStderr.text = taskStderrText
    }
}