package ru.moevm.moevm_checker.utils

import ru.moevm.moevm_checker.core.tasks.TaskConstants
import java.io.File

object Utils {
    fun buildFilePath(vararg pieceOfPath: String): String {
        return pieceOfPath.fold("") { prev, new ->
            File(prev, new).path
        }
    }

    fun isFileReadable(file: File): Boolean {
        return file.exists() && file.isFile && file.canRead()
    }

    fun isTaskEnvironmentExisted(path: String): Boolean {
        PluginLogger.d("Utils", "isTaskEnvironmentExisted: start checking task environment for $path")
        val taskFile = File(path, TaskConstants.TASK_FILE_NAME)
        return if (isFileReadable(taskFile)) {
            val reader = taskFile.bufferedReader()
            var isTaskEnvironmentValid: Boolean
            try {
                val isCourseIdFound = reader.readLine().startsWith(TaskConstants.COURSE_ID_FOR_TASK_FILE)
                val isTaskIdFound = reader.readLine().startsWith(TaskConstants.TASK_ID_FOR_TASK_FILE)

                PluginLogger.d(
                    "Utils",
                    "isTaskEnvironmentExisted: check value isCourseIdFound $isCourseIdFound, isTaskIdFound $isTaskIdFound"
                )
                isTaskEnvironmentValid = isCourseIdFound && isTaskIdFound
            } catch (e: Exception) {
                PluginLogger.d("Utils", "isTaskEnvironmentExisted: exception ${e.message}")
                isTaskEnvironmentValid = false
            } finally {
                reader.close()
            }

            isTaskEnvironmentValid
        } else {
            PluginLogger.d("Utils", "isTaskEnvironmentExisted: task file does not exist or not readable")
            false
        }
    }
}