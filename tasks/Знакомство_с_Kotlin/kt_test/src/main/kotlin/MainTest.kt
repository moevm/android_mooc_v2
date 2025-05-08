package ru.jengle88

import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

abstract class BaseTest {
    abstract val main: () -> Unit

    @Test
    fun testMainFunctionPrintsCorrectMessage() {
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            main()
            val output = outputStream.toString().trim()
            assertEquals("Hello brave citizen of glorious Bug Kingdom!", output)
            result += "1fa_dlqw"
        } finally {
            System.setOut(originalOut)
        }
    }

    companion object {

        private var result = ""

        @JvmStatic
        @AfterClass
        fun tearDown() {
            println("CHECKER $result")
        }
    }
}
