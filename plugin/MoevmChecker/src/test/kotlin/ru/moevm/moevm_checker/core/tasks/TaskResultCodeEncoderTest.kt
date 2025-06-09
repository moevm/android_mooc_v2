package ru.moevm.moevm_checker.core.tasks

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TaskResultCodeEncoderTest {

    private val underTest = TaskResultCodeEncoder()

    @Test
    fun `encode should return a non-null hash for valid inputs`() {
        val taskReference = TaskReference(courseId = "1", taskId = "2")
        val resultCode = "ABC123DE56"
        val timestamp = 123456789L

        val encodedResult = underTest.encode(taskReference, timestamp, resultCode)
        assertNotNull(encodedResult)
    }

    @Test
    fun `encode should generate the same hash for identical inputs at the same timestamp`() {
        val taskReference = TaskReference(courseId = "1", taskId = "2")
        val resultCode = "ABC123DE56"
        val timestamp = 193514721L

        val encodedResult1 = underTest.encode(taskReference, timestamp, resultCode)
        val encodedResult2 = underTest.encode(taskReference, timestamp, resultCode)
        assertEquals(encodedResult1, encodedResult2)
    }
}
