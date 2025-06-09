package ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper

import java.io.File

object TaskFileHashHelper {
    fun File.goDeepToDirectoryToSingleFolder(): File? {
        return if (this.exists() && this.isDirectory && this.listFiles().size == 1) {
            File(this.path, this.listFiles().first().name)
        } else {
            null
        }
    }
}
