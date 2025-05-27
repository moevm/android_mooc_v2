package com.example.checker_lib

import org.junit.AfterClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class Checker {

    abstract val mainFun: () -> Unit

    @Test
    fun testCorrectOutputWithDefaultName() {
        val input = "Alice"
        val expectedOutput = "Oh mighty ruler of Bug Kingdom, the earthling called $input seeks for your wisdom!"

        val originalIn = System.`in`
        val originalOut = System.out
        val inputStream = input.byteInputStream()
        val outputStream = java.io.ByteArrayOutputStream()

        try {
            System.setIn(inputStream)
            System.setOut(java.io.PrintStream(outputStream))

            mainFun()

            val actualOutput = outputStream.toString()
            assert(actualOutput == expectedOutput) {
                "Expected: $expectedOutput, but got: $actualOutput"
            }
            result += "al1_312fax"
        } finally {
            System.setIn(originalIn)
            System.setOut(originalOut)
        }
    }

    @Test
    fun testCorrectOutputWithBigName() {
        val input = buildString {
            append("Big ")
            append('B')
            repeat(200) {
                append('o')
            }
            append('b')
        }
        val expectedOutput = "Oh mighty ruler of Bug Kingdom, the earthling called $input seeks for your wisdom!"

        val originalIn = System.`in`
        val originalOut = System.out
        val inputStream = input.byteInputStream()
        val outputStream = java.io.ByteArrayOutputStream()

        try {
            System.setIn(inputStream)
            System.setOut(java.io.PrintStream(outputStream))

            mainFun()

            val actualOutput = outputStream.toString()
            assert(actualOutput == expectedOutput) {
                "Expected: $expectedOutput, but got: $actualOutput"
            }
            result += "laq1_222_ddaq"
        } finally {
            System.setIn(originalIn)
            System.setOut(originalOut)
        }
    }

    @Test
    fun testCorrectOutputWithNameWithDigits() {
        val input = "MyCoolName123"
        val expectedOutput = "Oh mighty ruler of Bug Kingdom, the earthling called $input seeks for your wisdom!"

        val originalIn = System.`in`
        val originalOut = System.out
        val inputStream = input.byteInputStream()
        val outputStream = java.io.ByteArrayOutputStream()

        try {
            System.setIn(inputStream)
            System.setOut(java.io.PrintStream(outputStream))

            mainFun()

            val actualOutput = outputStream.toString()
            assert(actualOutput == expectedOutput) {
                "Expected: $expectedOutput, but got: $actualOutput"
            }
            result += "perty_123_ff1"
        } finally {
            System.setIn(originalIn)
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