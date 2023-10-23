package ru.mse.moevm_checker.core.file_system.repository

import kotlinx.coroutines.flow.Flow
import ru.mse.moevm_checker.core.data.CoursesInfo

interface CoursesInfoRepository {
    fun isCourseInfoAvailable(coursesInfoState: CoursesInfo?): Boolean

    fun invalidateCourseInfoState(): Flow<CoursesInfo?>
}