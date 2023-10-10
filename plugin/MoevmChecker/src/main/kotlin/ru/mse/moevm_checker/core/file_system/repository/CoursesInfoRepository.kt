package ru.mse.moevm_checker.core.file_system.repository

import kotlinx.coroutines.flow.StateFlow
import ru.mse.moevm_checker.core.file_system.data.CoursesInfoState

interface CoursesInfoRepository {
    val coursesInfoState: StateFlow<CoursesInfoState?>

    fun isCourseInfoAvailable(): Boolean

    fun invalidateCourseInfoState(): StateFlow<CoursesInfoState?>
}