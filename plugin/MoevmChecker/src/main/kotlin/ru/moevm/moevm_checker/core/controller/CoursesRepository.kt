package ru.moevm.moevm_checker.core.controller

import kotlinx.coroutines.flow.*
import ru.moevm.moevm_checker.core.data.course.Course
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.data.course.CoursesInfo
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import ru.moevm.moevm_checker.core.network.GoogleFilesApi

interface CoursesRepository {
    fun initRepositoryFlow(force: Boolean): Flow<Boolean>

    fun getCoursesInfoFlow(): Flow<CoursesInfo>

    fun getTaskInfoFlow(courseId: String, taskId: String): Flow<CourseTask?>

    fun findCourseAndTaskByIdFlow(courseId: String, taskId: String): Flow<Pair<Course, CourseTask>?>
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

    override fun getCoursesInfoFlow(): Flow<CoursesInfo> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        emit(requireNotNull(coursesInfoMutableState.value))
    }

    override fun getTaskInfoFlow(courseId: String, taskId: String): Flow<CourseTask?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        emit(findTaskByCourseIdAndTaskId(courseId, taskId))
    }

    override fun findCourseAndTaskByIdFlow(courseId: String, taskId: String): Flow<Pair<Course, CourseTask>?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val courses = requireNotNull(coursesInfoMutableState.value?.courses)
        val course = courses.find { course -> course.id == courseId }
        val task = course?.courseTasks?.find { task -> task.id == taskId }
        if (course == null || task == null) {
            emit(null)
        } else {
            emit(course to task)
        }
    }

    private fun findTaskByCourseIdAndTaskId(courseId: String, taskId: String): CourseTask? {
        val courses = coursesInfoMutableState.value?.courses ?: return null
        val course = courses.find { it.id == courseId }
        val task = course?.courseTasks?.find { it.id == taskId }
        return task
    }
}