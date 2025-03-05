package ru.moevm.moevm_checker.core.controller

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import ru.moevm.moevm_checker.core.data.course.Course
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.data.course.CoursesInfo
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import ru.moevm.moevm_checker.core.network.GoogleFilesApi
import ru.moevm.moevm_checker.core.tasks.TaskReference

interface CoursesRepository {
    fun initRepositoryFlow(force: Boolean): Flow<Boolean>

    fun getCoursesInfoFlow(): Flow<CoursesInfo?>

    fun getCourseDescriptionFlow(courseId: String): Flow<String?>

    fun getTaskInfoFlow(taskReference: TaskReference): Flow<CourseTask?>

    fun getTaskDescriptionFlow(taskReference: TaskReference): Flow<String?>

    fun findCourseAndTaskByReferenceFlow(taskReference: TaskReference): Flow<Pair<Course, CourseTask>?>

    fun findTaskByReferenceFlow(taskReference: TaskReference): Flow<CourseTask?>
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
        val id = courseDescriptionUrl?.substringAfter("id=", "")?.substringBefore("&")
        if (currentCoroutineContext().isActive) {
            val result = id?.let { courseId -> googleFilesApi.getDescriptionByLinkParams(id = courseId).string() }
            emit(result)
        }
    }

    override fun getTaskInfoFlow(taskReference: TaskReference): Flow<CourseTask?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val task = findTaskByReferenceFlow(taskReference).last()
        emit(task)
    }

    override fun getTaskDescriptionFlow(taskReference: TaskReference): Flow<String?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val taskDescriptionUrl = findTaskByReferenceFlow(taskReference).single()?.taskDescriptionUrl
        val id = taskDescriptionUrl?.substringAfter("id=", "")?.substringBefore("&")
        if (currentCoroutineContext().isActive) {
            val result = id?.let { taskId -> googleFilesApi.getDescriptionByLinkParams(id = taskId).string() }
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
}