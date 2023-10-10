package ru.mse.moevm_checker.core.ui.coursesWindow

import com.intellij.ui.AnimatedIcon
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mse.moevm_checker.core.data.FileDownloadingStatus
import ru.mse.moevm_checker.core.di.DepsInjector
import ru.mse.moevm_checker.core.file_system.repository.CoursesFileValidator
import ru.mse.moevm_checker.core.network.FileDownloader
import ru.mse.moevm_checker.core.ui.BaseContent
import ru.mse.moevm_checker.core.ui.data.DialogPanelData
import ru.mse.moevm_checker.utils.Constants
import ru.mse.moevm_checker.utils.ResStr
import java.net.URL
import javax.swing.JButton

class NoMainFileContent(
    private val projectPath: String,
    private val onRefreshDialogPanelClick: () -> Unit,
    private val fileDownloader: FileDownloader = DepsInjector.provideFileDownloader(),
    private val coursesFileValidator: CoursesFileValidator = DepsInjector.provideCourseFileValidator()
) : BaseContent {
    /*  UI Components   */
    private lateinit var loadingProgressRow: Row
    private lateinit var loadingFailedRow: Row
    private lateinit var loadingSuccessRow: Row
    private lateinit var mainFileNotFoundRow: Row
    private lateinit var refreshButton: Cell<JButton>

    /*  UI Data     */
    private var isMainFileNotFoundLabelVisible = !coursesFileValidator.isMainCoursesFileValid()
    private var isLoadingMainCoursesFileInProgressVisible = false
    private var isLoadingMainCoursesFileSuccessVisible = false
    private var isLoadingMainCoursesFileFailedVisible = false
    private var isRefreshButtonVisible = false

    /*  Data    */
    private val mainFileName = ResStr.getString("dataMainCourseFileName")
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO)


    override fun getDialogPanel(): DialogPanelData {
        val panelName = "MOEVM Checker"
        val dialogPanel = createDialogPanel()

        return DialogPanelData(panelName, dialogPanel)
    }

    private fun createDialogPanel() = panel {
        panel {
            row {
                label(ResStr.getString("NoMainFileContentWelcomeTitle"))
            }

            mainFileNotFoundRow = row {
                label(ResStr.getString("NoMainFileContentMainFileNotFound"))
            }.apply {
                visible(isMainFileNotFoundLabelVisible)
            }

            loadingFailedRow = row {
                label(ResStr.getString("NoMainFileContentDownloadingFailed"))
            }.apply {
                visible(isLoadingMainCoursesFileFailedVisible)
            }

            loadingSuccessRow = row {
                label(ResStr.getString("NoMainFileContentDownloadingSuccess"))
            }.apply {
                visible(isLoadingMainCoursesFileSuccessVisible)
            }

            loadingProgressRow = row {
                icon(AnimatedIcon.Default())
                label(ResStr.getString("NoMainFileContentLoadingTitle"))
            }.apply {
                visible(isLoadingMainCoursesFileInProgressVisible)
            }

            row {
                button(ResStr.getString("NoMainFileContentDownloadMainFileButtonTitle")) {
                    downloadMainCoursesFile()
                }
                refreshButton = button(ResStr.getString("NoMainFileContentRefreshDialogPanelAfterDownload")) {
                    onRefreshDialogPanelClick()
                }.apply {
                    visible(isRefreshButtonVisible)
                }
            }
        }.align(Align.CENTER)
    }

    private fun downloadMainCoursesFile() {
        fileDownloader.downloadFile(mainFileName, projectPath, URL(Constants.URL_FILE_WITH_COURSES_INFO))
            .onEach { status ->
                handleStatus(status)
            }
            .launchIn(ioCoroutineScope)
    }

    private fun handleStatus(status: FileDownloadingStatus) {
        isMainFileNotFoundLabelVisible = (status is FileDownloadingStatus.Failed)
        isLoadingMainCoursesFileInProgressVisible = (status == FileDownloadingStatus.Downloading)
        isLoadingMainCoursesFileFailedVisible = (status is FileDownloadingStatus.Failed
            || (status is FileDownloadingStatus.Success && !coursesFileValidator.isMainCoursesFileValid()))
        isLoadingMainCoursesFileSuccessVisible = (status is FileDownloadingStatus.Success
            && coursesFileValidator.isMainCoursesFileValid())
        isRefreshButtonVisible = (status is FileDownloadingStatus.Success)
        refreshUiState()
    }

    private fun refreshUiState() {
        mainFileNotFoundRow.visible(isMainFileNotFoundLabelVisible)
        loadingProgressRow.visible(isLoadingMainCoursesFileInProgressVisible)
        loadingFailedRow.visible(isLoadingMainCoursesFileFailedVisible)
        loadingSuccessRow.visible(isLoadingMainCoursesFileSuccessVisible)
        refreshButton.visible(isRefreshButtonVisible)
    }
}