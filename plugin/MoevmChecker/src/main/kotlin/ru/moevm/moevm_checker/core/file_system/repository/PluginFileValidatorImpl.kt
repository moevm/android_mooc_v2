package ru.moevm.moevm_checker.core.file_system.repository

import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.plugin_utils.Utils
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File

class PluginFileValidatorImpl(
    private val projectEnvironmentInfo: ProjectEnvironmentInfo,
    private val coursesInfoReader: CoursesInfoReader
) : PluginFileValidator {

    override fun isMainCoursesFileValid(): Boolean {
        val file = File(projectEnvironmentInfo.rootDir, ResStr.getString("dataMainCourseFileName"))
        if (!Utils.isFileReadable(file)) {
            return false
        }
        return coursesInfoReader.readCourseInfo(file) != null
    }

    override fun isTaskFileValid(): Boolean {
        val file = File(File(projectEnvironmentInfo.rootDir), ResStr.getString("dataTaskFileName"))
        if (!file.exists()) {
            return false
        }
        // Проверка, существует ли MainCoursesFile
        return coursesInfoReader.readCourseInfoFromTaskFolder(file.parentFile) != null
    }
}