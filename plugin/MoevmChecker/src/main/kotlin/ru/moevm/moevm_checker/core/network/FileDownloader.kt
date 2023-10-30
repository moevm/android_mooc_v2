package ru.moevm.moevm_checker.core.network

import kotlinx.coroutines.flow.Flow
import ru.moevm.moevm_checker.core.data.FileDownloadingStatus
import java.net.URL

interface FileDownloader {
    fun downloadFile(fileName: String, outputDir: String, url: URL): Flow<FileDownloadingStatus>
}