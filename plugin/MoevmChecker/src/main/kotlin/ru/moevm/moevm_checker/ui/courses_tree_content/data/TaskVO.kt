package ru.moevm.moevm_checker.ui.courses_tree_content.data

import ru.moevm.moevm_checker.core.tasks.TaskFileStatus
import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesItemType

data class TaskVO(
    val courseId: String,
    val taskId: String,
    val name: String,
    val fileStatus: TaskFileStatus,
    val type: CoursesItemType,
)
