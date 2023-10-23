package ru.mse.moevm_checker.core.ui.no_main_file_ui

import kotlinx.coroutines.flow.Flow
import ru.mse.moevm_checker.core.data.FileDownloadingStatus

interface NoMainFileModel {

    val presenter: NoMainFilePresenter

    fun downloadMainCoursesFile(): Flow<FileDownloadingStatus>

}