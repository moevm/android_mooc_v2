package ru.mse.moevm_checker.core.di

import com.google.gson.Gson
import ru.mse.moevm_checker.core.common.CoroutineDispatchers
import ru.mse.moevm_checker.core.common.CoroutineDispatchersImpl
import ru.mse.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.mse.moevm_checker.core.file_system.reader.JsonCoursesInfoReaderImpl
import ru.mse.moevm_checker.core.file_system.repository.CoursesFileValidator
import ru.mse.moevm_checker.core.file_system.repository.CoursesFileValidatorImpl
import ru.mse.moevm_checker.core.file_system.repository.CoursesInfoRepository
import ru.mse.moevm_checker.core.file_system.repository.CoursesInfoRepositoryImpl
import ru.mse.moevm_checker.core.network.FileDownloader
import ru.mse.moevm_checker.core.network.FileDownloaderImpl
import ru.mse.moevm_checker.utils.ProjectEnvironmentInfo

object DepsInjector {
    val projectEnvironmentInfo
        get() = ProjectEnvironmentInfo

    private val coroutineDispatchers = CoroutineDispatchersImpl()

    fun provideDispatcher(): CoroutineDispatchers = coroutineDispatchers

    fun provideFileDownloader(): FileDownloader {
        return FileDownloaderImpl()
    }

    fun provideCourseFileValidator(
        coursesInfoReader: CoursesInfoReader = provideCoursesInfoReader(),
    ): CoursesFileValidator {
        return CoursesFileValidatorImpl(
            projectEnvironmentInfo.rootDir,
            coursesInfoReader
        )
    }

    fun provideCoursesInfoRepository(
        coursesInfoReader: CoursesInfoReader = provideCoursesInfoReader()
    ): CoursesInfoRepository {
        return CoursesInfoRepositoryImpl(
            projectEnvironmentInfo.rootDir,
            coursesInfoReader
        )
    }

    fun provideCoursesInfoReader(
        gson: Gson = provideGson()
    ): CoursesInfoReader {
        return JsonCoursesInfoReaderImpl(gson)
    }

    private fun provideGson(): Gson {
        return Gson()
    }
}