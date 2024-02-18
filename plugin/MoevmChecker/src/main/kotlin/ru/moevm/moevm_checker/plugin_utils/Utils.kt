package ru.moevm.moevm_checker.plugin_utils

import com.intellij.openapi.util.SystemInfo

object Utils {

    fun buildFilePath(withStartSplitter: Boolean, withEndSplitter: Boolean, vararg pieceOfPath: String): String {
        val splitter = if (SystemInfo.isWindows) {
            '\\'
        } else {
            '/'
        }
        return buildString {
            if (withStartSplitter) {
                append(splitter)
            }
            for ((index, pathElement) in pieceOfPath.withIndex()) {
                append(pathElement)
                if (index != pieceOfPath.lastIndex) {
                    append("$splitter")
                }
            }
            if (withEndSplitter) {
                append(splitter)
            }
        }
    }
}