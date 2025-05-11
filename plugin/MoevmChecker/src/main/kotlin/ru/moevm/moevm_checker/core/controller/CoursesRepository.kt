package ru.moevm.moevm_checker.core.controller

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import ru.moevm.moevm_checker.core.data.course.Course
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.data.course.CoursesInfo
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import ru.moevm.moevm_checker.core.network.GoogleFilesApi
import ru.moevm.moevm_checker.core.network.FileDownloadingStatus
import ru.moevm.moevm_checker.core.tasks.TaskConstants
import ru.moevm.moevm_checker.core.tasks.TaskReference
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

interface CoursesRepository {
    fun initRepositoryFlow(force: Boolean): Flow<Boolean>

    fun getCoursesInfoFlow(): Flow<CoursesInfo?>

    fun getCourseDescriptionFlow(courseId: String): Flow<String?>

    fun getTaskDescriptionFlow(taskReference: TaskReference): Flow<String?>

    fun findCourseAndTaskByReferenceFlow(taskReference: TaskReference): Flow<Pair<Course, CourseTask>?>

    fun findTaskByReferenceFlow(taskReference: TaskReference): Flow<CourseTask?>

    fun downloadTaskArchiveByLinkFlow(taskReference: TaskReference, outputDir: String): Flow<FileDownloadingStatus>
}

class CoursesRepositoryImpl(
    private val googleFilesApi: GoogleFilesApi,
) : CoursesRepository {

    private val coursesInfoMutableState = MutableStateFlow<CoursesInfo?>(null)

    override fun initRepositoryFlow(force: Boolean): Flow<Boolean> = flowSafe {
        if (!force && coursesInfoMutableState.value != null) {
            emit(true)
            return@flowSafe
        }
        emit(false)
        val result = googleFilesApi.getCoursesInfo()
        coursesInfoMutableState.value = result
        emit(true)
    }

    override fun getCoursesInfoFlow(): Flow<CoursesInfo?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        emit(coursesInfoMutableState.value)
    }

    override fun getCourseDescriptionFlow(courseId: String): Flow<String?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val courseDescriptionUrl =
            coursesInfoMutableState.value?.courses?.find { course -> course.id == courseId }?.courseDescriptionUrl
        if (currentCoroutineContext().isActive && courseDescriptionUrl != null) {
            val result = googleFilesApi.getDescriptionByLinkParams(courseDescriptionUrl).string()
            emit(result)
        }
    }

    override fun getTaskDescriptionFlow(taskReference: TaskReference): Flow<String?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val taskDescriptionUrl = findTaskByReferenceFlow(taskReference).single()?.taskDescriptionUrl
        if (currentCoroutineContext().isActive && taskDescriptionUrl != null) {
            val result = googleFilesApi.getDescriptionByLinkParams(taskDescriptionUrl).string()
            emit(result)
        }
    }

    override fun findCourseAndTaskByReferenceFlow(taskReference: TaskReference): Flow<Pair<Course, CourseTask>?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val courses = coursesInfoMutableState.value?.courses
        val course = courses?.find { course -> course.id == taskReference.courseId }
        val task = course?.courseTasks?.find { task -> task.id == taskReference.taskId }
        if (course == null || task == null) {
            emit(null)
        } else {
            emit(course to task)
        }
    }

    override fun findTaskByReferenceFlow(taskReference: TaskReference): Flow<CourseTask?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val courses = coursesInfoMutableState.value?.courses
        val course = courses?.find { it.id == taskReference.courseId }
        val task = course?.courseTasks?.find { it.id == taskReference.taskId }
        emit(task)
    }

    override fun downloadTaskArchiveByLinkFlow(
        taskReference: TaskReference,
        outputDir: String
    ): Flow<FileDownloadingStatus> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val task = findTaskByReferenceFlow(taskReference).single()
        val url = task?.archiveUrl
        if (url != null) {
            emit(FileDownloadingStatus.Downloading)
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                val archiveName = TaskConstants.getTaskArchiveNameByTaskId(taskReference.taskId)
                val outputFile = File(outputDir, archiveName)
                inputStream = googleFilesApi.downloadTaskArchiveByLink(url).byteStream()
                outputStream = FileOutputStream(outputFile)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
                emit(FileDownloadingStatus.Success(file = outputFile))
            } catch (e: Exception) {
                emit(FileDownloadingStatus.Failed(message = e.message ?: "Downloading failed"))
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } else {
            emit(FileDownloadingStatus.Failed("Task archive URL is null"))
        }
    }
}