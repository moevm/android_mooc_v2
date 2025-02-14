package ru.moevm.moevm_checker.core.tasks

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.filesystem.TaskFileLauncherPermissionStrategy
import ru.moevm.moevm_checker.core.network.downloading.FileDownloader
import ru.moevm.moevm_checker.core.network.downloading.FileDownloadingStatus
import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodePlatform
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import ru.moevm.moevm_checker.utils.Utils
import java.io.File
import java.net.URL
import java.util.zip.ZipFile

class TaskFileManagerImpl(
    private val coursesRepository: CoursesRepository,
    private val taskFileLauncherPermissionStrategy: TaskFileLauncherPermissionStrategy,
    private val fileDownloader: FileDownloader,
    private val projectConfig: ProjectConfigProvider
) : TaskFileManager {
    override fun isTaskFilesExist(taskId: String): Boolean {
        val (course, _) = coursesRepository.findCourseAndTaskByTaskId(taskId) ?: return false
        val rootDir = projectConfig.rootDir ?: return false
        val taskFileName = TaskManager.getTaskFileNameByTaskId(taskId)
        val taskFolder = File(Utils.buildFilePath(rootDir, course.name, taskFileName))
        return taskFolder.exists()
    }

    override fun downloadTaskFiles(taskId: String): Flow<TaskDownloadStatus> {
        val (course, task) = coursesRepository.findCourseAndTaskByTaskId(taskId) ?: return flowSafe {
            emit(
                TaskDownloadStatus.FAILED_BEFORE_START
            )
        }
        val rootDir = projectConfig.rootDir ?: return flowSafe { emit(TaskDownloadStatus.FAILED_BEFORE_START) }
        val taskFileName = TaskManager.getTaskFileNameByTaskId(taskId)
        val zipArchiveName = "$taskFileName.zip"
        val outputCourseDir = Utils.buildFilePath(rootDir, course.name)
        val taskFolder = File(Utils.buildFilePath(rootDir, course.name, taskFileName))
        if (!taskFolder.exists()) {
            taskFolder.mkdirs()
        }
        return flowSafe {
            emit(TaskDownloadStatus.DOWNLOADING)
            val fileStatus = fileDownloader.downloadFile(zipArchiveName, outputCourseDir, URL(task.archiveUrl)).last()
            var file: File? = null
            when (fileStatus) {
                is FileDownloadingStatus.Failed -> {
                    println("downloading failed, taskid = $taskId, ${fileStatus.message}")
                    emit(TaskDownloadStatus.DOWNLOAD_FAILED)
                    return@flowSafe
                }

                FileDownloadingStatus.Downloading -> {
                    println("downloading failed, taskid = $taskId, last state is Downloading")
                    emit(TaskDownloadStatus.DOWNLOAD_FAILED)
                    return@flowSafe
                }

                is FileDownloadingStatus.Success -> {
                    file = fileStatus.file
                }
            }
            emit(TaskDownloadStatus.DOWNLOAD_FINISH)
            emit(TaskDownloadStatus.UNZIPPING)
            unzipZipArchive(file, taskFolder.path)
            file.delete()
            val taskCodePlatform = TaskCodePlatform.values().find { it.type == task.courseTaskPlatform }
            if (taskCodePlatform == null) {
                println("unzipping failed, cannot determinate taskCodePlatform")
                emit(TaskDownloadStatus.UNZIPPING_FAILED)
            } else {
                taskFileLauncherPermissionStrategy.setLauncherAsExecutable(taskCodePlatform, taskFolder)
                val newTaskFile = File(taskFolder.path, ".task_file")
                if (newTaskFile.createNewFile()) {
                    if (!newTaskFile.canWrite()) {
                        newTaskFile.setWritable(true)
                    }
                    newTaskFile.writer().use {
                        it.append("${TaskFileManager.COURSE_ID_FOR_TASK_FILE}${course.id}")
                        it.appendLine()
                        it.append("${TaskFileManager.TASK_ID_FOR_TASK_FILE}${task.id}")
                    }
                } else {
                    emit(TaskDownloadStatus.UNZIPPING_FAILED)
                }
                File(taskFolder.path, ".task_file").createNewFile()
                emit(TaskDownloadStatus.UNZIPPING_FINISH)
            }
        }
    }

    private fun unzipZipArchive(file: File, outputDir: String) {
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

    override fun removeTaskFiles(taskId: String): Flow<TaskRemoveStatus> {
        val (course, _) = coursesRepository.findCourseAndTaskByTaskId(taskId) ?: return flowSafe {
            emit(
                TaskRemoveStatus.FAILED_BEFORE_START
            )
        }
        val rootDir = projectConfig.rootDir ?: return flowSafe { emit(TaskRemoveStatus.FAILED_BEFORE_START) }
        val taskFileName = TaskManager.getTaskFileNameByTaskId(taskId)
        val taskFolder = File(Utils.buildFilePath(rootDir, course.name, taskFileName))
        return flowSafe {
            emit(TaskRemoveStatus.REMOVING)
            if (taskFolder.deleteRecursively()) {
                emit(TaskRemoveStatus.REMOVE_FINISHED)
            } else {
                emit(TaskRemoveStatus.REMOVE_FAILED)
            }
        }
    }
}