package ru.moevm.moevm_checker.core.tasks

import kotlinx.coroutines.flow.Flow

interface TaskFileManager {
    fun isTaskFilesExist(taskId: String): Boolean
    fun downloadTaskFiles(taskId: String): Flow<TaskDownloadStatus>
    fun removeTaskFiles(taskId: String): Flow<TaskRemoveStatus>

    companion object {
        const val COURSE_ID_FOR_TASK_FILE = "course_id="
        const val TASK_ID_FOR_TASK_FILE = "task_id="
        const val TASK_FILE_NAME = ".task_file"
    }
}
