package ru.moevm.moevm_checker.core.file_system.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import ru.moevm.moevm_checker.core.data.Course
import ru.moevm.moevm_checker.core.data.CourseTask
import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.plugin_utils.catchLog
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File

class CoursesInfoRepositoryImpl(
    private val projectEnvironmentInfo: ProjectEnvironmentInfo,
    private val coursesInfoReader: CoursesInfoReader
) : CoursesInfoRepository {
    private val mainCourseFileName = ResStr.getString("dataMainCourseFileName")

    override fun invalidateCourseInfoState(): Flow<CoursesInfo?> {
        return flow {
            val file = File(projectEnvironmentInfo.rootDir, mainCourseFileName)
            val coursesInfo = coursesInfoReader.readCourseInfo(file)
            emit(coursesInfo)
        }.catchLog()

    }

    override fun findTaskAndCourseByTaskId(taskId: String): Pair<Course, CourseTask>? {
        // FIXME придумать что-нибудь получше
        return runBlocking {
            val courses = invalidateCourseInfoState().last()?.courses ?: return@runBlocking null
            var selectedTask: CourseTask? = null
            var selectedCourse: Course? = null
            for (course in courses) {
                selectedTask = course.courseTasks.find { it.id == taskId }
                if (selectedTask != null) {
                    selectedCourse = course
                    break
                }
            }
            if (selectedCourse == null || selectedTask == null)
                return@runBlocking null
            return@runBlocking selectedCourse to selectedTask
        }

    }
}