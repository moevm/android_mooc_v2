package ru.moevm.moevm_checker.core.ui.courses_ui

interface CoursesPresenter {
    val coursesModel: CoursesModel
    val coursesContentView: CoursesContentView

    fun onCoursesContentViewCreated()

    fun onRefreshCoursesInfo()
}