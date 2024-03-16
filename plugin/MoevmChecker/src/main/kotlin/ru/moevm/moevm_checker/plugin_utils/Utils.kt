package ru.moevm.moevm_checker.plugin_utils

import com.intellij.openapi.util.SystemInfo
import java.io.File

object Utils {

    fun buildFilePath(vararg pieceOfPath: String): String {
        val splitter = if (SystemInfo.isWindows) {
            '\\'
        } else {
            '/'
        }
        return buildString {
            for ((index, pathElement) in pieceOfPath.withIndex()) {
                append(pathElement)
                if (index != pieceOfPath.lastIndex) {
                    append("$splitter")
                }
            }
        }
    }

    fun isFileReadable(file: File): Boolean {
        return file.exists() && file.isFile && file.canRead()
    }
}