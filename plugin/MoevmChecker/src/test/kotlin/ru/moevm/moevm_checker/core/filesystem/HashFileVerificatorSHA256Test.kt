package ru.moevm.moevm_checker.core.filesystem

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

class HashFileVerificatorSHA256Test {

    private val underTest = HashFileVerificatorSHA256()

    @Test
    fun verifyTwoSameFilesHash() {
        val tempFile1 = createTempFile(prefix = "tempFile1_", suffix = ".txt")
        val tempFile2 = createTempFile(prefix = "tempFile2_", suffix = ".txt")

        tempFile1.writeText("Hello World!!!")
        tempFile2.writeText("Hello World!!!")

        assertTrue(underTest.computeHashCode(tempFile1.toFile()) == underTest.computeHashCode(tempFile2.toFile()))
    }

    @Test
    fun verifyTwoDifferentFilesHash() {
        val tempFile1 = createTempFile(prefix = "tempFile1_", suffix = ".txt")
        val tempFile2 = createTempFile(prefix = "tempFile2_", suffix = ".txt")

        tempFile1.writeText("Hello World!!!1")
        tempFile2.writeText("Hello World!!!2")

        assertTrue(underTest.computeHashCode(tempFile1.toFile()) != underTest.computeHashCode(tempFile2.toFile()))
    }

    @Test
    fun verifyFileHashWithSha256Hash() {
        val tempFile = createTempFile(prefix = "tempFile_", suffix = ".txt")
        val rightHashSHA256 = "7f7ca6b928342c9fa100c22dce57e234d079306deb4b046a2dff21fd689d2468"
        tempFile.writeText("Hello World with hashing!!!")

        assertTrue(
            underTest.verifyFilesHash(
                listOf(
                    HashFileVerificator.FileWithHash(
                        tempFile.toFile(),
                        rightHashSHA256
                    )
                )
            )
        )
    }
}