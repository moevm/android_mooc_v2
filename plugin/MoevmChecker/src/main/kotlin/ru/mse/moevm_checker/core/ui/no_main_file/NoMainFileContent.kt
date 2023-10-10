package ru.mse.moevm_checker.core.ui.no_main_file

import com.intellij.ui.AnimatedIcon
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import ru.mse.moevm_checker.core.ui.BaseContent
import ru.mse.moevm_checker.core.ui.data.DialogPanelData
import ru.mse.moevm_checker.utils.ResStr
import javax.swing.JButton

class NoMainFileContent(
    projectPath: String,
) : BaseContent, NoMainFileContentView {
    /*  UI Components   */
    private lateinit var loadingProgressRow: Row
    private lateinit var loadingFailedRow: Row
    private lateinit var loadingSuccessRow: Row
    private lateinit var mainFileNotFoundRow: Row
    private lateinit var refreshButton: Cell<JButton>

    private val presenter = NoMainFilePresenterImpl(
        this,
        ResStr.getString("dataMainCourseFileName"),
        projectPath
    )

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "MOEVM Checker"
        val dialogPanel = createDialogPanel()
        refreshUiState(
            isMainFileNotFoundLabelVisible = !presenter.isMainCoursesFileValid(),
            isLoadingMainCoursesFileInProgressVisible = false,
            isLoadingMainCoursesFileFailedVisible = false,
            isLoadingMainCoursesFileSuccessVisible = false,
            isRefreshButtonVisible = false
        )

        return DialogPanelData(panelName, dialogPanel)
    }

    private fun createDialogPanel() = panel {
        panel {
            row {
                label(ResStr.getString("NoMainFileContentWelcomeTitle"))
            }

            mainFileNotFoundRow = row {
                label(ResStr.getString("NoMainFileContentMainFileNotFound"))
            }

            loadingFailedRow = row {
                label(ResStr.getString("NoMainFileContentDownloadingFailed"))
            }

            loadingSuccessRow = row {
                label(ResStr.getString("NoMainFileContentDownloadingSuccess"))
            }

            loadingProgressRow = row {
                icon(AnimatedIcon.Default())
                label(ResStr.getString("NoMainFileContentLoadingTitle"))
            }

            row {
                button(ResStr.getString("NoMainFileContentDownloadMainFileButtonTitle")) {
                    presenter.onDownloadMainFileClick()
                }
                refreshButton = button(ResStr.getString("NoMainFileContentRefreshDialogPanelAfterDownload")) {
                    presenter.onRefreshViewClick()
                }
            }
        }.align(Align.CENTER)
    }

    override fun refreshUiState(isMainFileNotFoundLabelVisible: Boolean, isLoadingMainCoursesFileInProgressVisible: Boolean, isLoadingMainCoursesFileFailedVisible: Boolean, isLoadingMainCoursesFileSuccessVisible: Boolean, isRefreshButtonVisible: Boolean) {
        mainFileNotFoundRow.visible(isMainFileNotFoundLabelVisible)
        loadingProgressRow.visible(isLoadingMainCoursesFileInProgressVisible)
        loadingFailedRow.visible(isLoadingMainCoursesFileFailedVisible)
        loadingSuccessRow.visible(isLoadingMainCoursesFileSuccessVisible)
        refreshButton.visible(isRefreshButtonVisible)
    }
}