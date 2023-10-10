package ru.mse.moevm_checker.core.data


import com.google.gson.annotations.SerializedName

data class Task(
        @SerializedName("archive_url")
        val archiveUrl: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("task_description")
        val taskDescription: String,
        @SerializedName("type")
        val type: String
)