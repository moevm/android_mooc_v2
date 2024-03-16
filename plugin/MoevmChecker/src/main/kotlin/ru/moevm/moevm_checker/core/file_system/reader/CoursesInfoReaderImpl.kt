package ru.moevm.moevm_checker.core.file_system.reader

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.task.Task
import ru.moevm.moevm_checker.core.task.TaskBuilder
import ru.moevm.moevm_checker.core.task.TaskPlatformType
import ru.moevm.moevm_checker.plugin_utils.Utils
import ru.moevm.moevm_checker.utils.ResStr
import java.io.File

class CoursesInfoReaderImpl(
    private val gson: Gson,
    private val taskBuilder: TaskBuilder
) : CoursesInfoReader {
    override fun readCourseInfo(file: File): CoursesInfo? {
        var data: CoursesInfo? = null
        try {
            val text = file.readText()
            data = gson.fromJson(text, CoursesInfo::class.java)
        } catch (e: JsonSyntaxException) {
            println("readCourseInfo ${e.message}")
        }
        return data
    }

    override fun readCourseInfoFromTaskFolder(taskFolder: File): CoursesInfo? {
        val mainCoursesFolder = taskFolder.parentFile.parentFile
        val mainCoursesFile = File(mainCoursesFolder.path, ResStr.getString("dataMainCourseFileName"))
        if (!Utils.isFileReadable(mainCoursesFile)) {
            println("MainCoursesFile cannot exist")
            return null
        }
        return readCourseInfo(mainCoursesFile)
    }

    override fun readTaskDescription(taskFolder: File): String? {
        val taskFile = File(taskFolder, ResStr.getString("dataTaskDescriptionFileName"))
        if (!Utils.isFileReadable(taskFile)) {
            println("File with task description cannot exist")
            return null
        }
        return taskFile.readText()
    }

    override fun readTask(taskFolder: File): Task? {
        val mainCoursesFolder = taskFolder.parentFile.parentFile
        val mainCoursesFile = File(mainCoursesFolder.path, ResStr.getString("dataMainCourseFileName"))
        val courseInfo = readCourseInfo(mainCoursesFile) ?: return null

        val course = taskFolder.parentFile.name
        val taskId = taskFolder.name.takeLastWhile { it.isDigit() }
        val taskInfo = courseInfo.courses
            .find { it.name == course }?.courseTasks
            ?.find { it.id == taskId }
            ?: return null
        val taskType = TaskPlatformType.values()
            .find { it.platformName == taskInfo.courseTaskPlatform }
            ?: return null

        return taskBuilder.buildTask(taskType, taskId)
    }
}