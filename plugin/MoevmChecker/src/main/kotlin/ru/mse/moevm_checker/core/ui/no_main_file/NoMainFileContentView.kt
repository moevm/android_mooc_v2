package ru.mse.moevm_checker.core.ui.no_main_file

interface NoMainFileContentView {

    fun refreshUiState(
        isMainFileNotFoundLabelVisible: Boolean,
        isLoadingMainCoursesFileInProgressVisible: Boolean,
        isLoadingMainCoursesFileFailedVisible: Boolean,
        isLoadingMainCoursesFileSuccessVisible: Boolean,
        isRefreshButtonVisible: Boolean,
    )
}