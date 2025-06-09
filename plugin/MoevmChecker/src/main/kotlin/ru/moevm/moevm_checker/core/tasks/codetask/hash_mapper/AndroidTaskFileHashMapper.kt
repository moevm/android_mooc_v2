package ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper

import ru.moevm.moevm_checker.core.data.course.HashFileSource
import ru.moevm.moevm_checker.core.data.course.TaskFileHash
import ru.moevm.moevm_checker.core.filesystem.HashFileVerificator
import ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper.TaskFileHashHelper.goDeepToDirectoryToSingleFolder
import ru.moevm.moevm_checker.utils.Utils
import java.io.File

class AndroidTaskFileHashMapper : TaskFileHashMapper {
    override fun map(taskFileHash: TaskFileHash, taskFolder: File): HashFileVerificator.FileWithHash? {
        return when (taskFileHash.fileType) {
            HashFileSource.TEST_ARCHIVE.str -> {
                val fileToArchive = File(Utils.buildFilePath(taskFolder.path, "libs", "checker_lib-release.aar"))
                if (fileToArchive.exists() && fileToArchive.isFile) {
                    HashFileVerificator.FileWithHash(
                        file = fileToArchive,
                        hash = taskFileHash.hash
                    )
                } else {
                    null
                }
            }
            HashFileSource.TEST_INSTRUMENTAL_LAUNCHER.str -> {
                val basePathToTest = File(Utils.buildFilePath(taskFolder.path, "app", "src", "androidTest", "java"))
                val testFile = tryGetTestFile(basePathToTest, "ExampleInstrumentedTest.kt")
                if (testFile != null) {
                    HashFileVerificator.FileWithHash(
                        file = testFile,
                        hash = taskFileHash.hash
                    )
                } else {
                    null
                }
            }
            HashFileSource.TEST_UNIT_LAUNCHER.str -> {
                val basePathToTest = File(Utils.buildFilePath(taskFolder.path, "app", "src", "test", "java"))
                val testFile = tryGetTestFile(basePathToTest, "ExampleUnitTest.kt")
                if (testFile != null) {
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

    private fun tryGetTestFile(basePathToTest: File, endpointFileName: String): File? {
        val pathToTest = basePathToTest
            .goDeepToDirectoryToSingleFolder() // to com
            ?.goDeepToDirectoryToSingleFolder() // to example
            ?.goDeepToDirectoryToSingleFolder() // to <project_name>
        return if (pathToTest != null && pathToTest.exists() && pathToTest.listFiles().size == 1 && pathToTest.listFiles()
                .first().name == endpointFileName
        ) {
            pathToTest.listFiles().first()
        } else {
            null
        }
    }
}