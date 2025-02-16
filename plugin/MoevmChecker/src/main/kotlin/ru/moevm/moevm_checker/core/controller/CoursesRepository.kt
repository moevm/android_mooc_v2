package ru.moevm.moevm_checker.core.controller

import kotlinx.coroutines.flow.*
import ru.moevm.moevm_checker.core.data.course.Course
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.data.course.CoursesInfo
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import ru.moevm.moevm_checker.core.network.GoogleFilesApi

interface CoursesRepository {
    fun initRepository(force: Boolean): Flow<Boolean>

    fun getCoursesInfo(): Flow<CoursesInfo>

    fun getTaskInfo(courseId: String, taskId: String): Flow<CourseTask?>

    fun findCourseAndTaskByTaskId(taskId: String): Pair<Course, CourseTask>?
}

class CoursesRepositoryImpl(
    private val googleFilesApi: GoogleFilesApi,
) : CoursesRepository {

    private val coursesInfoMutableState = MutableStateFlow<CoursesInfo?>(null)

    override fun initRepository(force: Boolean): Flow<Boolean> = flowSafe {
        if (!force && coursesInfoMutableState.value != null) {
            emit(true)
            return@flowSafe
        }
        emit(false)
        val result = googleFilesApi.getCoursesInfo()
        coursesInfoMutableState.value = result
        emit(true)
    }

    override fun getCoursesInfo(): Flow<CoursesInfo> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepository(false)
        }
        emit(requireNotNull(coursesInfoMutableState.value))
    }

    override fun getTaskInfo(courseId: String, taskId: String): Flow<CourseTask?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepository(false)
        }
        emit(findTaskByCourseIdAndTaskId(courseId, taskId))
    }

    override fun findCourseAndTaskByTaskId(taskId: String): Pair<Course, CourseTask>? {
        val courses = coursesInfoMutableState.value?.courses ?: return null
        var selectedTask: CourseTask? = null
        var selectedCourse: Course? = null
        for (course in courses) {
            selectedTask = course.courseTasks.find { it.id == taskId }
            if (selectedTask != null) {
                selectedCourse = course
                break
            }
        }
        if (selectedCourse == null || selectedTask == null)
            return null
        return selectedCourse to selectedTask
    }

    private fun findTaskByCourseIdAndTaskId(courseId: String, taskId: String): CourseTask? {
        val courses = coursesInfoMutableState.value?.courses ?: return null
        val course = courses.find { it.id == courseId }
        val task = course?.courseTasks?.find { it.id == taskId }
        return task
    }
}