package ru.moevm.moevm_checker.core.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url
import ru.moevm.moevm_checker.core.data.course.CoursesInfo

interface GoogleFilesApi {

    @GET("/uc")
    suspend fun getCoursesInfo(
        @Query("export") export: String = "download",
        @Query("id") id: String = "10_19KkRMer-ImMu70r0EcUF2Dp9FDLm5",
    ): CoursesInfo

    // загрузка описания курса или задачи
    @GET
    suspend fun getDescriptionByLinkParams(@Url descriptionUrl: String): ResponseBody

    @GET
    @Streaming
    suspend fun downloadTaskArchiveByLink(@Url archiveUrl: String): ResponseBody
}