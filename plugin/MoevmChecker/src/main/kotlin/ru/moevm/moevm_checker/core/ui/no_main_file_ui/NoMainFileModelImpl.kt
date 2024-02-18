package ru.moevm.moevm_checker.core.ui.no_main_file_ui

import kotlinx.coroutines.flow.Flow
import ru.moevm.moevm_checker.core.data.FileDownloadingStatus
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.network.FileDownloader
import ru.moevm.moevm_checker.utils.Constants
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import java.net.URL

class NoMainFileModelImpl(
    override val presenter: NoMainFilePresenter,
    private val mainFileName: String,
    private val fileDownloader: FileDownloader = DepsInjector.provideFileDownloader(),
    private val projectEnvironmentInfo: ProjectEnvironmentInfo = DepsInjector.projectEnvironmentInfo,
) : NoMainFileModel {

    override fun downloadMainCoursesFile(): Flow<FileDownloadingStatus> {
        return fileDownloader.downloadFile(
            mainFileName,
            projectEnvironmentInfo.rootDir,
            URL(Constants.URL_FILE_WITH_COURSES_INFO)
        )
    }
}