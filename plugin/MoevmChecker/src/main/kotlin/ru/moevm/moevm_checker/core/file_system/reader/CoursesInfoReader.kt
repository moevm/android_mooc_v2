package ru.moevm.moevm_checker.core.file_system.reader

import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.task.Task
import java.io.File

interface CoursesInfoReader {
    fun readCourseInfo(file: File): CoursesInfo?

    fun readCourseInfoFromTaskFolder(taskFolder: File): CoursesInfo?

    fun readTaskDescription(taskFolder: File): String?

    fun readTask(taskFolder: File): Task?
}