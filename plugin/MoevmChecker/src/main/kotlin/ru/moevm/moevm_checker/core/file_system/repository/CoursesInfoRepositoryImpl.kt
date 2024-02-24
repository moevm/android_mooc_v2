package ru.moevm.moevm_checker.core.file_system.repository

import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File

class CoursesInfoRepositoryImpl(
    private val projectEnvironmentInfo: ProjectEnvironmentInfo,
    private val coursesInfoReader: CoursesInfoReader
) : CoursesInfoRepository {
    private val mainCourseFileName = ResStr.getString("dataMainCourseFileName")

    override fun invalidateCourseInfoState(): CoursesInfo? {
        val file = File(projectEnvironmentInfo.rootDir, mainCourseFileName)
        val coursesInfo = coursesInfoReader.readCourseInfo(file)
        return coursesInfo
    }
}