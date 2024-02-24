package ru.moevm.moevm_checker.core.file_system.repository

import ru.moevm.moevm_checker.core.data.CoursesInfo

interface CoursesInfoRepository {
    fun invalidateCourseInfoState(): CoursesInfo?
}