package ru.mse.moevm_checker.core.ui.no_main_file_ui

interface NoMainFilePresenter {
    val noMainFileModel: NoMainFileModel
    val noMainFileContentView: NoMainFileContentView

    fun onDownloadMainFileClick()

    fun onRefreshViewClick()

    fun isMainCoursesFileValid(): Boolean
}