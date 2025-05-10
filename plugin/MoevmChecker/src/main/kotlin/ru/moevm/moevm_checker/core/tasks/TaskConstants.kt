package ru.moevm.moevm_checker.core.tasks

object TaskConstants {
    const val COURSE_ID_FOR_TASK_FILE = "course_id="
    const val TASK_ID_FOR_TASK_FILE = "task_id="
    const val TASK_FILE_NAME = ".task_file"

    const val TASK_DESCRIPTION_NAME = "task_description.md"

    const val CHECKER_FLAG = "CHECKER"

    @JvmStatic
    fun getTaskFileNameByTaskId(taskId: String): String = "task${taskId}"

    @JvmStatic
    fun getTaskArchiveNameByTaskId(taskId: String): String = "${getTaskFileNameByTaskId(taskId)}.zip"
}