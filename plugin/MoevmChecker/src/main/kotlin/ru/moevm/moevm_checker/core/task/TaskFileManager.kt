package ru.moevm.moevm_checker.core.task

import kotlinx.coroutines.flow.Flow

interface TaskFileManager {
    /**
     * should use in IO thread
     */
    fun downloadTaskFiles(taskId: String): Flow<TaskDownloadStatus>?

    fun removeTaskFiles(taskId: String)
}