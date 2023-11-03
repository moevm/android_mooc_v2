package ru.moevm.moevm_checker.core.data


import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("archive_url")
    val archiveUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("task_description")
    val taskDescription: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("version")
    val version: String
)