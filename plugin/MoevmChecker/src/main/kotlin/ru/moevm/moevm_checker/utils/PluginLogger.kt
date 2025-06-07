package ru.moevm.moevm_checker.utils

import java.io.File
import java.io.FileOutputStream
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*

private const val isLogToFileEnabled = true

object PluginLogger {
    private var loggerFileWriter: Writer? = null

    fun setFile(pathToRoot: String) {
        if (!isLogToFileEnabled) {
            return
        }
        val loggerFolder = File(pathToRoot, ".checker").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val loggerFile = File(
            loggerFolder.path,
            SimpleDateFormat("dd.MM.yy").format(Date().time) + ".txt"
        ).apply {
            if (!exists()) {
                createNewFile()
            }
        }
        loggerFileWriter = FileOutputStream(
            loggerFile,
            true,
        ).writer().apply {
            appendLine("${getCurrentTime()} -----------------")
        }
    }

    fun d(tag: String, msg: String) {
        if (isLogToFileEnabled) {
            loggerFileWriter?.let { writer ->
                writer.appendLine(buildLogMessage("D", tag, msg))
                writer.flush()
            }
        }
        println(buildLogMessage("D", tag, msg))
    }

    // Debug inner, no log file
    fun di(tag: String, msg: String) {
        println(buildLogMessage("D", tag, msg))
    }

    fun close() {
        d("END", "closed")
        loggerFileWriter?.close()
        loggerFileWriter = null
    }

    private fun buildLogMessage(level: String, tag: String, msg: String): String {
        return "${getCurrentTime()} $tag: [$level] [${getCurrentThread()}] $msg"
    }
    private fun getCurrentTime() = SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").format(Date())
    private fun getCurrentThread() = Thread.currentThread().name
}