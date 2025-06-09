package ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper

import ru.moevm.moevm_checker.core.data.course.HashFileSource
import ru.moevm.moevm_checker.core.data.course.TaskFileHash
import ru.moevm.moevm_checker.core.filesystem.HashFileVerificator
import ru.moevm.moevm_checker.utils.Utils
import java.io.File

class KotlinTaskFileHashMapper : TaskFileHashMapper {
    override fun map(
        taskFileHash: TaskFileHash,
        taskFolder: File
    ): HashFileVerificator.FileWithHash? {
        return when (taskFileHash.fileType) {
            HashFileSource.TEST_ARCHIVE.str -> {
                val fileToArchive = File(Utils.buildFilePath(taskFolder.path, "libs", "checker_lib-release.jar"))
                if (fileToArchive.exists() && fileToArchive.isFile) {
                    HashFileVerificator.FileWithHash(
                        file = fileToArchive,
                        hash = taskFileHash.hash
                    )
                } else {
                    null
                }
            }

            HashFileSource.TEST_UNIT_LAUNCHER.str -> {
                val basePathToTest = File(Utils.buildFilePath(taskFolder.path, "src", "test", "kotlin"))
                val testFile = File(basePathToTest, "Test.kt")
                if (testFile.exists() && testFile.isFile) {
                    HashFileVerificator.FileWithHash(
                        file = testFile,
                        hash = taskFileHash.hash
                    )
                } else {
                    null
                }
            }

            else -> null
        }
    }
}