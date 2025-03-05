package ru.moevm.moevm_checker.core.data.course


import com.google.gson.annotations.SerializedName

data class CourseTask(
    @SerializedName("archive_url")
    val archiveUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("course_task_platform")
    val courseTaskPlatform: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("version")
    val version: String,
    @SerializedName("task_description_url")
    val taskDescriptionUrl: String
)