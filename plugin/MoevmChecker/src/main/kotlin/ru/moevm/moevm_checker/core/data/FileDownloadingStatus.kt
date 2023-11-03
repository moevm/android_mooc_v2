package ru.moevm.moevm_checker.core.data

import java.io.File

sealed class FileDownloadingStatus(val file: File? = null, val message: String? = null) {
    object Downloading : FileDownloadingStatus()

    class Success(file: File) : FileDownloadingStatus(file = file)

    class Failed(message: String) : FileDownloadingStatus(message = message)
}