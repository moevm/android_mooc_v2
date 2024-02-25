package ru.moevm.moevm_checker.core.task

enum class TaskDownloadStatus {
    DOWNLOADING,
    DOWNLOAD_FINISH,
    DOWNLOAD_FAILED,
    UNZIPPING,
    UNZIPPING_FINISH,
    UNZIPPING_FAILED
}