package ru.moevm.moevm_checker.core.task

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import ru.moevm.moevm_checker.core.data.FileDownloadingStatus
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepository
import ru.moevm.moevm_checker.core.network.FileDownloader
import ru.moevm.moevm_checker.plugin_utils.Utils
import ru.moevm.moevm_checker.plugin_utils.catchLog
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File
import java.net.URL
import java.util.zip.ZipFile

class TaskFileManagerImpl(
    private val coursesInfoRepository: CoursesInfoRepository,
    private val fileDownloader: FileDownloader,
    private val projectEnvironmentInfo: ProjectEnvironmentInfo
) : TaskFileManager {

    override fun downloadTaskFiles(taskId: String): Flow<TaskDownloadStatus>? {
        val (course, task) = coursesInfoRepository.findTaskAndCourseByTaskId(taskId) ?: return null
        val taskName = TaskManager.getTaskFileNameByTaskId(taskId)
        val zipArchiveName = "$taskName.zip"
        val outputCourseDir = Utils.buildFilePath(projectEnvironmentInfo.rootDir, course.name)
        val taskFolder = File(Utils.buildFilePath(projectEnvironmentInfo.rootDir, course.name, taskName))
        if (!taskFolder.exists()) {
            taskFolder.mkdirs()
        }
        return flow {
            emit(TaskDownloadStatus.DOWNLOADING)
            val fileStatus = fileDownloader.downloadFile(zipArchiveName, outputCourseDir, URL(task.archiveUrl)).last()
            val file = fileStatus.file
            if (fileStatus is FileDownloadingStatus.Failed || file == null || !file.exists()) {
                emit(TaskDownloadStatus.DOWNLOAD_FAILED)
                return@flow
            }
            emit(TaskDownloadStatus.DOWNLOAD_FINISH)

            emit(TaskDownloadStatus.UNZIPPING)
            unzipArchive(file, taskFolder.path)
            file.delete()
            // создаём новый файл, чтобы в дальнейшем плагин мог определять папку с заданием
            File(Utils.buildFilePath(taskFolder.path, ResStr.getString("dataTaskFileName"))).createNewFile()
            emit(TaskDownloadStatus.UNZIPPING_FINISH)
        }.catchLog()
    }

    private fun unzipArchive(file: File, outputDir: String) {
        ZipFile(file).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    if (entry.isDirectory) {
                        val d = File(outputDir, entry.name)
                        if (!d.exists()) d.mkdirs()
                    } else {
                        val f = File(outputDir, entry.name)
                        if (f.parentFile?.exists() != true) {
                            f.parentFile?.mkdirs()
                        }
                        f.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }

    override fun removeTaskFiles(taskId: String) {
        TODO()
    }
}