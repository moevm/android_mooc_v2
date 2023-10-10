package ru.mse.moevm_checker.core.ui.no_main_file

interface NoMainFilePresenter {
    val noMainFileModel: NoMainFileModel
    val noMainFileContentView: NoMainFileContentView

    fun onDownloadMainFileClick()

    fun onRefreshViewClick()
    fun isMainCoursesFileValid(): Boolean
}