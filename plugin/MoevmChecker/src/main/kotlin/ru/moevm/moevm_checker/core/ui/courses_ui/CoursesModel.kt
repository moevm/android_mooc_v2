package ru.moevm.moevm_checker.core.ui.courses_ui

import ru.moevm.moevm_checker.core.data.CoursesInfo

interface CoursesModel {
    val presenter: CoursesPresenter

    fun forceInvalidateCoursesInfo(): CoursesInfo?
}