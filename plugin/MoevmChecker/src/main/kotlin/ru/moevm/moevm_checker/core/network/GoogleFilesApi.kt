package ru.moevm.moevm_checker.core.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.moevm.moevm_checker.core.data.course.CoursesInfo

interface GoogleFilesApi {

    @GET("/uc")
    suspend fun getCoursesInfo(
        @Query("export") export: String = "download",
        @Query("id") id: String = "10_19KkRMer-ImMu70r0EcUF2Dp9FDLm5",
    ): CoursesInfo
}