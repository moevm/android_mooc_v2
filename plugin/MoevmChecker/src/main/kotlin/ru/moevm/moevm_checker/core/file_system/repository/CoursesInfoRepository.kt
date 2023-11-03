package ru.moevm.moevm_checker.core.file_system.repository

import kotlinx.coroutines.flow.Flow
import ru.moevm.moevm_checker.core.data.CoursesInfo

interface CoursesInfoRepository {
    fun isCourseInfoAvailable(coursesInfoState: CoursesInfo?): Boolean

    fun invalidateCourseInfoState(): Flow<CoursesInfo?>
}