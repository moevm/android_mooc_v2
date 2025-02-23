package ru.moevm.moevm_checker.dagger

import com.intellij.openapi.util.SystemInfo
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.moevm.moevm_checker.core.controller.AuthController
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.controller.CoursesRepositoryImpl
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.filesystem.TaskFileLauncherPermissionStrategyMacOS
import ru.moevm.moevm_checker.core.filesystem.TaskFileLauncherPermissionStrategy
import ru.moevm.moevm_checker.core.filesystem.TaskFileLauncherPermissionStrategyStub
import ru.moevm.moevm_checker.core.network.GoogleFilesApi
import ru.moevm.moevm_checker.core.network.downloading.FileDownloader
import ru.moevm.moevm_checker.core.network.downloading.FileDownloaderImpl
import ru.moevm.moevm_checker.core.tasks.TaskFileManager
import ru.moevm.moevm_checker.core.tasks.TaskFileManagerImpl
import ru.moevm.moevm_checker.core.tasks.TaskManager
import ru.moevm.moevm_checker.core.tasks.TaskManagerImpl
import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCheckSystem
import ru.moevm.moevm_checker.core.tasks.codetask.platforms.android.AndroidCodeTaskCheckSystem
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object AppModule {
// TODO Пересмотреть использование Singleton и заменить на Scope

    @Provides
    @Singleton
    @AndroidCheckSystem
    fun provideAndroidCheckSystem(
        projectConfigProvider: ProjectConfigProvider,
    ): AbstractCheckSystem {
        return AndroidCodeTaskCheckSystem(projectConfigProvider.jdkPath)
    }

    @Provides
    @Singleton
    fun provideTaskManager(
        coursesRepository: CoursesRepository,
        projectConfigProvider: ProjectConfigProvider,
        @Ui dispatcher: CoroutineDispatcher
    ): TaskManager {
        return TaskManagerImpl(coursesRepository, projectConfigProvider, dispatcher)
    }

    @Provides
    @Singleton
    fun provideTaskFileLauncherPermissionStrategy(): TaskFileLauncherPermissionStrategy {
        return when {
            SystemInfo.isMac -> TaskFileLauncherPermissionStrategyMacOS()
            else -> TaskFileLauncherPermissionStrategyStub
        }
    }

    @Provides
    @Singleton
    fun provideTaskFileManager(
        coursesRepository: CoursesRepository,
        taskFileLauncherPermissionStrategy: TaskFileLauncherPermissionStrategy,
        fileDownloader: FileDownloader,
        projectConfig: ProjectConfigProvider,
    ): TaskFileManager {
        return TaskFileManagerImpl(
            coursesRepository,
            taskFileLauncherPermissionStrategy,
            fileDownloader,
            projectConfig,
        )
    }

    @Provides
    @Singleton
    fun provideFileDownloader(): FileDownloader {
        return FileDownloaderImpl()
    }

    @Provides
    @Singleton
    fun provideCoursesRepository(googleFilesApi: GoogleFilesApi): CoursesRepository {
        return CoursesRepositoryImpl(googleFilesApi)
    }

    @Provides
    @Singleton
    fun provideAuthController(): AuthController {
        return AuthController()
    }

    @Provides
    @Singleton
    fun provideGoogleFilesApi(retrofitBuilder: Retrofit.Builder): GoogleFilesApi {
        return retrofitBuilder
            .baseUrl("https://drive.google.com")
            .build()
            .create(GoogleFilesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }
}