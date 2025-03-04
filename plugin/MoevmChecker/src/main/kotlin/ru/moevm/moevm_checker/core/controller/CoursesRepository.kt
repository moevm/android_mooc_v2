package ru.moevm.moevm_checker.core.controller

import kotlinx.coroutines.flow.*
import ru.moevm.moevm_checker.core.data.course.Course
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.data.course.CoursesInfo
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import ru.moevm.moevm_checker.core.network.GoogleFilesApi
import ru.moevm.moevm_checker.core.tasks.TaskReference

interface CoursesRepository {
    fun initRepositoryFlow(force: Boolean): Flow<Boolean>

    fun getCoursesInfoFlow(): Flow<CoursesInfo?>

    fun getTaskInfoFlow(taskReference: TaskReference): Flow<CourseTask?>

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

    override fun getTaskInfoFlow(taskReference: TaskReference): Flow<CourseTask?> = flowSafe {
        if (coursesInfoMutableState.value == null) {
            initRepositoryFlow(false).last()
        }
        val task = findTaskByReferenceFlow(taskReference).last()
        emit(task)
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