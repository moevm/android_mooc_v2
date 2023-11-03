package ru.moevm.moevm_checker.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.moevm.moevm_checker.core.data.FileDownloadingStatus
import java.io.File
import java.io.IOException
import java.net.URL

class FileDownloaderImpl : FileDownloader {

    /**
     * should use in IO thread
     */
    override fun downloadFile(fileName: String, outputDir: String, url: URL): Flow<FileDownloadingStatus> {
        return flow {
            try {
                emit(FileDownloadingStatus.Downloading)
                val inputStream = url.openStream()
                val file = File(outputDir, fileName)
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                emit(FileDownloadingStatus.Success(file = file))
            } catch (e: IOException) {
                println(e.message)
                emit(FileDownloadingStatus.Failed(message = e.message ?: "downloadFile exception"))
            }
        }
    }
}