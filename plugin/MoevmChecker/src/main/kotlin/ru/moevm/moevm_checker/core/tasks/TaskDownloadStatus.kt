package ru.moevm.moevm_checker.core.tasks

enum class TaskDownloadStatus {
    FAILED_BEFORE_START,
    DOWNLOADING,
    DOWNLOAD_FINISH,
    DOWNLOAD_FAILED,
    UNZIPPING,
    UNZIPPING_FINISH,
    UNZIPPING_FAILED,
}