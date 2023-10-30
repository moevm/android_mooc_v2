package ru.moevm.moevm_checker.core.file_system.repository

import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File

class CoursesFileValidatorImpl(
    private val rootDir: String,
    private val coursesInfoReader: CoursesInfoReader
) : CoursesFileValidator {
    private val mainCourseFileName = ResStr.getString("dataMainCourseFileName")

    override fun isMainCoursesFileValid(): Boolean {
        val file = File(rootDir, mainCourseFileName)
        if (!file.exists() || !file.canRead()) {
            return false
        }
        return coursesInfoReader.readCourseInfo(file) != null
    }

    override fun isTaskFileValid(): Boolean {
//        TODO("Not yet implemented")
        return false
    }
}