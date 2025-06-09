package ru.moevm.moevm_checker.core.tasks.codetask.hash_mapper

import ru.moevm.moevm_checker.core.data.course.TaskFileHash
import ru.moevm.moevm_checker.core.filesystem.HashFileVerificator
import java.io.File

interface TaskFileHashMapper {
    fun map(taskFileHash: TaskFileHash, taskFolder: File): HashFileVerificator.FileWithHash?
}
