package ru.moevm.moevm_checker.core.network.downloading

import kotlinx.coroutines.flow.Flow
import ru.moevm.moevm_checker.core.utils.coroutine.flowSafe
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

class FileDownloaderImpl : FileDownloader {
    override fun downloadFile(fileName: String, outputDir: String, url: URL): Flow<FileDownloadingStatus> {
        return flowSafe {
            var inputStream: InputStream? = null
            try {
                emit(FileDownloadingStatus.Downloading)
                inputStream = url.openStream()
                val file = File(outputDir, fileName)
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                emit(FileDownloadingStatus.Success(file = file))
            } catch (e: IOException) {
                emit(FileDownloadingStatus.Failed(message = e.message ?: "FileDownloadingStatus is Failed"))
            } finally {
                inputStream?.close()
            }
        }
    }
}