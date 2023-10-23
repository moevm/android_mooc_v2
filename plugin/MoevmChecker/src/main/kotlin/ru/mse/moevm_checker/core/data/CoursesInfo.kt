package ru.mse.moevm_checker.core.data


import com.google.gson.annotations.SerializedName

data class CoursesInfo(
    @SerializedName("courses")
    val courses: List<Course>,
    @SerializedName("version")
    val version: String
)