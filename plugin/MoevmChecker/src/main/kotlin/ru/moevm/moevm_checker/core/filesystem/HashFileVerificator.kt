package ru.moevm.moevm_checker.core.filesystem

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

interface HashFileVerificator {
    fun verifyFilesHash(files: List<FileWithHash>): Boolean

    /**
     Visible only for testing, use [verifyFilesHash] instead
     */
    fun computeHashCode(file: File): String

    data class FileWithHash(val file: File, val hash: String)
}

class HashFileVerificatorSHA256: HashFileVerificator {
    override fun verifyFilesHash(files: List<HashFileVerificator.FileWithHash>): Boolean {
        for (file in files) {
            if (!verifyFileHash(file)) {
                return false
            }
        }
        return true
    }

    private fun verifyFileHash(file: HashFileVerificator.FileWithHash): Boolean {
        return computeHashCode(file.file) == file.hash
    }

    override fun computeHashCode(file: File): String {
        val algorithm = "SHA-256"
        val digest = MessageDigest.getInstance(algorithm)
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}