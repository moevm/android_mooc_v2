package ru.mse.moevm_checker.core.ui.no_main_file_ui

interface NoMainFileContentView {

    val presenter: NoMainFilePresenter

    fun refreshUiState(
        isMainFileNotFoundLabelVisible: Boolean,
        isLoadingMainCoursesFileInProgressVisible: Boolean,
        isLoadingMainCoursesFileFailedVisible: Boolean,
        isLoadingMainCoursesFileSuccessVisible: Boolean,
        isRefreshButtonVisible: Boolean,
    )
}