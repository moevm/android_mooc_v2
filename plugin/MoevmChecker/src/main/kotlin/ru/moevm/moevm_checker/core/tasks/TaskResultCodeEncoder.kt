package ru.moevm.moevm_checker.core.tasks

import java.util.Base64

class TaskResultCodeEncoder {
    fun encode(taskReference: TaskReference, timestamp: Long, resultCode: String): String {
        val taskLocation = taskReference.courseId + "#" + taskReference.taskId
        val hashResult = Base64.getEncoder().withoutPadding().encodeToString(
            (taskLocation + SPLITTER + resultCode + SPLITTER + timestamp.toString()).toByteArray(Charsets.UTF_8)
        )
        return hashResult
    }



    fun decode(code: String): String {
        val decodedString = String(Base64.getDecoder().decode(code), Charsets.UTF_8)
        val parts = decodedString.split(SPLITTER)

        val taskLocation = parts[0] // courseId#taskId
        val resultCode = parts[1]
        val timestamp = parts[2].toLong()

        return "{$taskLocation, $resultCode, $timestamp}"
    }

    private companion object {
        const val SPLITTER = "##"
    }
}