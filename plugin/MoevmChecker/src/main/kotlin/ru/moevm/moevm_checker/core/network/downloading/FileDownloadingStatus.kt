package ru.moevm.moevm_checker.core.network.downloading

import java.io.File

sealed class FileDownloadingStatus {
    data object Downloading : FileDownloadingStatus()

    class Success(val file: File) : FileDownloadingStatus()

    class Failed(val message: String) : FileDownloadingStatus()
}