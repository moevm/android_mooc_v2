package ru.moevm.moevm_checker.utils

import java.io.File

object Utils {
    fun buildFilePath(vararg pieceOfPath: String): String {
        return pieceOfPath.fold("") { prev, new ->
            File(prev, new).path
        }
    }

    fun isFileReadable(file: File): Boolean {
        return file.exists() && file.isFile && file.canRead()
    }
}