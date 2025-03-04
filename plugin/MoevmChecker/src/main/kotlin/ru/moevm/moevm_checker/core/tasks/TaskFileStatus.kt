package ru.moevm.moevm_checker.core.tasks

enum class TaskFileStatus {
    NOT_DOWNLOADABLE,
    AVAILABLE,
    DOWNLOADING,
    DOWNLOADED,
    REMOVING,
    OUTDATED,
}
