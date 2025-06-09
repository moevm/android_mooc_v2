package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import ru.moevm.moevm_checker.core.data.course.TaskFileHash
import ru.moevm.moevm_checker.core.filesystem.HashFileVerificator
import ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper.TaskFileHashMapper
import java.io.File

class TaskFilesHashVerificator(
    private val hashFileVerificator: HashFileVerificator,
    private val taskFileHash: List<TaskFileHash>,
    private val mapper: TaskFileHashMapper,
) {
    fun verify(taskFolder: File): Boolean {
        val taskFileHashesMapped = taskFileHash.map { taskFileHash -> mapper.map(taskFileHash, taskFolder) }
        return taskFileHashesMapped.all { it != null } && hashFileVerificator.verifyFilesHash(taskFileHashesMapped.map {
            requireNotNull(
                it
            )
        })
    }
}
