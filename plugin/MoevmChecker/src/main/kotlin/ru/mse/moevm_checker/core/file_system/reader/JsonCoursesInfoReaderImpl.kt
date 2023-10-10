package ru.mse.moevm_checker.core.file_system.reader

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.mse.moevm_checker.core.data.CoursesInfo
import java.io.File

class JsonCoursesInfoReaderImpl(
    private val gson: Gson
) : CoursesInfoReader {
    override fun readCourseInfo(file: File): CoursesInfo? {
        var data: CoursesInfo? = null
        try {
            val text = file.readText()
            data = gson.fromJson(text, CoursesInfo::class.java)
        } catch (e: JsonSyntaxException) {
            println("JsonCoursesInfoReaderImpl::readCourseInfo ${e.message}")
        }
        return data
    }
}