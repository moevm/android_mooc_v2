package ru.moevm.moevm_checker.core.network.downloading

import kotlinx.coroutines.flow.Flow
import java.net.URL

interface FileDownloader {
    fun downloadFile(fileName: String, outputDir: String, url: URL): Flow<FileDownloadingStatus>
}