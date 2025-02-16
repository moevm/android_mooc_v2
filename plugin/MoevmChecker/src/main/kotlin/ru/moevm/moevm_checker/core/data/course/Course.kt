package ru.moevm.moevm_checker.core.data.course


import com.google.gson.annotations.SerializedName

data class Course(
    @SerializedName("course_description")
    val courseDescription: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tasks")
    val courseTasks: List<CourseTask>,
    @SerializedName("version")
    val version: String
)