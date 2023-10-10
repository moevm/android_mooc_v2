package ru.mse.moevm_checker.core.file_system.reader

import ru.mse.moevm_checker.core.data.CoursesInfo
import java.io.File

interface CoursesInfoReader {
    fun readCourseInfo(file: File): CoursesInfo?
}