package ru.moevm.moevm_checker.core.di

import com.google.gson.Gson
import ru.moevm.moevm_checker.core.common.CoroutineDispatchers
import ru.moevm.moevm_checker.core.common.CoroutineDispatchersImpl
import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReaderImpl
import ru.moevm.moevm_checker.core.file_system.repository.PluginFileValidator
import ru.moevm.moevm_checker.core.file_system.repository.PluginFileValidatorImpl
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepository
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepositoryImpl
import ru.moevm.moevm_checker.core.network.FileDownloader
import ru.moevm.moevm_checker.core.network.FileDownloaderImpl
import ru.moevm.moevm_checker.core.task.TaskBuilder
import ru.moevm.moevm_checker.core.task.TaskFileManager
import ru.moevm.moevm_checker.core.task.TaskFileManagerImpl
import ru.moevm.moevm_checker.core.task.TaskManager
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo

object DepsInjector {
    val projectEnvironmentInfo = ProjectEnvironmentInfo()

    private var coroutineDispatchers: CoroutineDispatchers? = null
    private val taskManager = TaskManager(
        projectEnvironmentInfo,
        provideCoursesInfoRepository(),
        provideTaskBuilder(),
        provideDispatcher().worker,
        provideDispatcher().ui,
    )

    fun provideDispatcher(): CoroutineDispatchers {
        if (coroutineDispatchers == null) {
            coroutineDispatchers = CoroutineDispatchersImpl()
        }
        return requireNotNull(coroutineDispatchers)
    }

    fun provideFileDownloader(): FileDownloader {
        return FileDownloaderImpl()
    }

    fun providePluginFileValidatorImpl(
        coursesInfoReader: CoursesInfoReader = provideCoursesInfoReader(),
    ): PluginFileValidator {
        return PluginFileValidatorImpl(
            projectEnvironmentInfo,
            coursesInfoReader
        )
    }

    fun provideCoursesInfoRepository(
        coursesInfoReader: CoursesInfoReader = provideCoursesInfoReader()
    ): CoursesInfoRepository {
        return CoursesInfoRepositoryImpl(
            projectEnvironmentInfo,
            coursesInfoReader
        )
    }

    fun provideCoursesInfoReader(
        gson: Gson = provideGson(),
        taskBuilder: TaskBuilder = provideTaskBuilder()
    ): CoursesInfoReader {
        return CoursesInfoReaderImpl(gson, taskBuilder)
    }

    private fun provideGson(): Gson {
        return Gson()
    }

    private fun provideTaskBuilder(): TaskBuilder {
        return TaskBuilder()
    }

    fun provideTaskManager(): TaskManager {
        return taskManager
    }

    fun provideTaskFileManager(
        coursesInfoRepository: CoursesInfoRepository = provideCoursesInfoRepository(),
        fileDownloader: FileDownloader = provideFileDownloader(),
    ): TaskFileManager {
        return TaskFileManagerImpl(coursesInfoRepository, fileDownloader, projectEnvironmentInfo)
    }
}