package ru.moevm.moevm_checker.dagger

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.ui.auth_content.AuthViewModel
import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesTreeViewModel
import ru.moevm.moevm_checker.ui.task.TaskViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface PluginComponent {

    val authViewModel: AuthViewModel

    val coursesTreeViewModel: CoursesTreeViewModel

    val taskViewModel: TaskViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance projectConfig: ProjectConfigProvider,
            @BindsInstance @Ui uiDispatcher: CoroutineDispatcher,
            @BindsInstance @Io ioDispatcher: CoroutineDispatcher,
            @BindsInstance @Work workDispatcher: CoroutineDispatcher,
        ): PluginComponent
    }
}