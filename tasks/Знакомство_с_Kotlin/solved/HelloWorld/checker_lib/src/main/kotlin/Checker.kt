package com.example.checker_lib

import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.System

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class Checker {

    abstract val mainFun: () -> Unit

    @Test
    fun testCorrectOut() {
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            mainFun()

            val correctStr = "Hello brave citizen of glorious Bug Kingdom!"
            val isCorrect = with(outputStream.toString()) {
                this == correctStr
            }
            assertTrue(isCorrect)
            result += "1ijfa_af1f_a"
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