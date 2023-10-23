package ru.mse.moevm_checker.core.ui.no_main_file_ui

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mse.moevm_checker.core.data.FileDownloadingStatus
import ru.mse.moevm_checker.core.di.DepsInjector
import ru.mse.moevm_checker.core.file_system.repository.CoursesFileValidator

class NoMainFilePresenterImpl(
    override val noMainFileContentView: NoMainFileContentView,
    mainFileName: String,
    projectPath: String,
    private val coursesFileValidator: CoursesFileValidator = DepsInjector.provideCourseFileValidator(),
    private val ioDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().worker
) : NoMainFilePresenter {
    override val noMainFileModel: NoMainFileModel = NoMainFileModelImpl(
        this,
        mainFileName,
        projectPath
    )

    override fun onDownloadMainFileClick() {
        noMainFileModel.downloadMainCoursesFile()
            .onEach { status ->
                handleStatus(status)
            }
            .launchIn(CoroutineScope(ioDispatcher))
    }

    override fun onRefreshViewClick() {
        TODO("Not yet implemented")
    }

    override fun isMainCoursesFileValid(): Boolean {
        return coursesFileValidator.isMainCoursesFileValid()
    }

    private fun handleStatus(status: FileDownloadingStatus) {
        val isMainFileNotFoundLabelVisible = (status is FileDownloadingStatus.Failed)
        val isLoadingMainCoursesFileInProgressVisible = (status == FileDownloadingStatus.Downloading)
        val isLoadingMainCoursesFileFailedVisible = (status is FileDownloadingStatus.Failed
            || (status is FileDownloadingStatus.Success && !coursesFileValidator.isMainCoursesFileValid()))
        val isLoadingMainCoursesFileSuccessVisible = (status is FileDownloadingStatus.Success
            && coursesFileValidator.isMainCoursesFileValid())
        val isRefreshButtonVisible = (status is FileDownloadingStatus.Success)

        noMainFileContentView.refreshUiState(
            isMainFileNotFoundLabelVisible,
            isLoadingMainCoursesFileInProgressVisible,
            isLoadingMainCoursesFileFailedVisible,
            isLoadingMainCoursesFileSuccessVisible,
            isRefreshButtonVisible
        )
    }
}