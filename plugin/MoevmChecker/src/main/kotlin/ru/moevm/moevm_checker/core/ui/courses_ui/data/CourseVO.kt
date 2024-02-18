package ru.moevm.moevm_checker.core.ui.courses_ui.data

data class CourseVO(
    val courseId: String,
    val courseName: String,
    val courseTaskPlatform: String,
    val courseTasks: List<TaskVO>
)
