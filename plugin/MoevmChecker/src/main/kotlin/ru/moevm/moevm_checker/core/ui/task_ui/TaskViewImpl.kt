package ru.moevm.moevm_checker.core.ui.task_ui

import com.intellij.openapi.project.Project
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import ru.moevm.moevm_checker.core.ui.BaseContent
import ru.moevm.moevm_checker.core.ui.data.DialogPanelData
import ru.moevm.moevm_checker.utils.ResStr
import javax.swing.JEditorPane

class TaskViewImpl(project: Project) : BaseContent, TaskView {
    /*  UI Components   */
    private lateinit var textResult: JEditorPane
    private lateinit var textStdout: JEditorPane
    private lateinit var textStderr: JEditorPane
    private lateinit var testing: Row

    override val presenter = TaskPresenterImpl(
        this,
        project
    )

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "Task checker"
        val dialogPanel = createDialogPanel()
        refreshUiState(
            isLoadingLabelVisible = false,
            taskResultText = "",
            taskStdoutText = "",
            taskStderrText = ""
        )
        return DialogPanelData(panelName, dialogPanel)
    }

    private fun createDialogPanel() = panel {
        row {
            button(ResStr.getString("TaskCheckButtonText")) {
                refreshUiState(
                    isLoadingLabelVisible = true,
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

    override fun refreshUiState(
        isLoadingLabelVisible: Boolean,
        taskResultText: String,
        taskStdoutText: String,
        taskStderrText: String
    ) {
        testing.visible(isLoadingLabelVisible)
        textResult.text = taskResultText
        textStdout.text = taskStdoutText
        textStderr.text = taskStderrText
    }
}