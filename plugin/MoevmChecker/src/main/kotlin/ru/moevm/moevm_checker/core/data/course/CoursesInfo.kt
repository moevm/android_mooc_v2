package ru.moevm.moevm_checker.core.data.course


import com.google.gson.annotations.SerializedName

data class CoursesInfo(
    @SerializedName("courses")
    val courses: List<Course>,
)