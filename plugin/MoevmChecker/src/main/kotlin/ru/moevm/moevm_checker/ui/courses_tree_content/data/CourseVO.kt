package ru.moevm.moevm_checker.ui.courses_tree_content.data

data class CourseVO(
    val id: String,
    val name: String,
    val tasks: List<TaskVO>
)
