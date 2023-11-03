package ru.moevm.moevm_checker.core.file_system.repository

interface CoursesFileValidator {
    fun isMainCoursesFileValid(): Boolean

    fun isTaskFileValid(): Boolean
}