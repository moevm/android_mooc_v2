package ru.moevm.moevm_checker.core.ui.courses_ui

import kotlinx.coroutines.flow.Flow
import ru.moevm.moevm_checker.core.data.CoursesInfo

interface CoursesModel {
    val presenter: CoursesPresenter

    fun forceInvalidateCoursesInfo(): Flow<CoursesInfo?>
}