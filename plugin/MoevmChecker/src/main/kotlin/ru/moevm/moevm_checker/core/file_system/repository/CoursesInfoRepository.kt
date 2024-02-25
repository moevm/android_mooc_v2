package ru.moevm.moevm_checker.core.file_system.repository

import kotlinx.coroutines.flow.Flow
import ru.moevm.moevm_checker.core.data.Course
import ru.moevm.moevm_checker.core.data.CourseTask
import ru.moevm.moevm_checker.core.data.CoursesInfo

interface CoursesInfoRepository {
    fun invalidateCourseInfoState(): Flow<CoursesInfo?>

    fun findTaskAndCourseByTaskId(taskId: String): Pair<Course, CourseTask>?
}