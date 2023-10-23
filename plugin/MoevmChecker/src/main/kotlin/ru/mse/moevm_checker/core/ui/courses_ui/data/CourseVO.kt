package ru.mse.moevm_checker.core.ui.courses_ui.data

data class CourseVO(
    val courseId: String,
    val courseName: String,
    val courseTasks: List<TaskVO>
)
