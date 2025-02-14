package ru.moevm.moevm_checker.core.tasks

enum class TaskRemoveStatus {
    FAILED_BEFORE_START,
    REMOVING,
    REMOVE_FINISHED,
    REMOVE_FAILED,
}