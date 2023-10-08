package ru.mse.moevm_checker.core.file_system.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ru.mse.moevm_checker.core.data.FileDownloadingStatus
import ru.mse.moevm_checker.core.file_system.data.CoursesInfoState
import ru.mse.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.mse.moevm_checker.core.network.FileDownloader
import ru.mse.moevm_checker.utils.Constants
import ru.mse.moevm_checker.utils.ResStr
import java.net.URL

class CoursesInfoRepositoryImpl(
    private val rootDir: String,
    private val fileDownloader: FileDownloader,
    private val coursesInfoReader: CoursesInfoReader
) : CoursesInfoRepository {
    private val coursesInfoMutableState = MutableStateFlow<CoursesInfoState>(CoursesInfoState.NoData)
    override val coursesInfoState: StateFlow<CoursesInfoState> = coursesInfoMutableState.asStateFlow()
    private val mainCourseFileName = ResStr.getString("dataMainCourseFileName")

    override fun isCourseInfoAvailable(): Boolean {
        return coursesInfoMutableState.value != CoursesInfoState.NoData
    }

    override fun invalidateCourseInfoState(): StateFlow<CoursesInfoState> {
        fileDownloader.downloadFile(mainCourseFileName, rootDir, URL(Constants.URL_FILE_WITH_COURSES_INFO))
            .onEach { status -> handleFileDownloadStatus(status) }
            .launchIn(CoroutineScope(Dispatchers.IO))

        return coursesInfoState
    }

    private fun handleFileDownloadStatus(status: FileDownloadingStatus) {
        when (status) {
            FileDownloadingStatus.Downloading -> { /* do nothing */ }
            is FileDownloadingStatus.Failed -> {
                if (coursesInfoMutableState.value is CoursesInfoState.ActualData) {
                    coursesInfoMutableState.value = CoursesInfoState.OutdatedData(requireNotNull(coursesInfoMutableState.value.coursesInfo))
                }
            }
            is FileDownloadingStatus.Success -> {
                val file = requireNotNull(status.file)
                val coursesInfo = coursesInfoReader.readCourseInfo(file)
                if (coursesInfo == null) {
                    coursesInfoMutableState.value = CoursesInfoState.NoData
                } else {
                    coursesInfoMutableState.value = CoursesInfoState.ActualData(coursesInfo)
                }
            }
        }
    }
}