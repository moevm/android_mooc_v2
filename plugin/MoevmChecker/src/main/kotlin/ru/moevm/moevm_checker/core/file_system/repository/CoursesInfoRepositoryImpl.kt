package ru.moevm.moevm_checker.core.file_system.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File

class CoursesInfoRepositoryImpl(
    private val rootDir: String,
    private val coursesInfoReader: CoursesInfoReader
) : CoursesInfoRepository {
    private val mainCourseFileName = ResStr.getString("dataMainCourseFileName")

    override fun isCourseInfoAvailable(coursesInfoState: CoursesInfo?): Boolean {
        return coursesInfoState != null
    }

    override fun invalidateCourseInfoState(): Flow<CoursesInfo?> {
        return flow {
            val file = File(rootDir, mainCourseFileName)
            val coursesInfo = coursesInfoReader.readCourseInfo(file)
            emit(coursesInfo)
        }
    }
}