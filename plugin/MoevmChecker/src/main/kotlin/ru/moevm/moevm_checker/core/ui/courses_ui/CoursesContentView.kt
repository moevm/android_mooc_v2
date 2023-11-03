package ru.moevm.moevm_checker.core.ui.courses_ui

import ru.moevm.moevm_checker.core.ui.courses_ui.data.CourseVO

interface CoursesContentView {

    val presenter: CoursesPresenter

    fun refreshUiState(courses: List<CourseVO>)
}