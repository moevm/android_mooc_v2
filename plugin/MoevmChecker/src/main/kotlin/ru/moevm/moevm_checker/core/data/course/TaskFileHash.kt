package ru.moevm.moevm_checker.core.data.course

import com.google.gson.annotations.SerializedName

data class TaskFileHash(
    @SerializedName("file_type")
    val fileType: String,
    @SerializedName("hash")
    val hash: String,
)
