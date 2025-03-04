package ru.moevm.moevm_checker.ui.task.android

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFileManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.tasks.TaskManager
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.tasks.codetask.AbstractCheckSystem
import ru.moevm.moevm_checker.dagger.AndroidCheckSystem
import ru.moevm.moevm_checker.dagger.Io
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import ru.moevm.moevm_checker.ui.task.TaskResultData
import java.io.File
import javax.inject.Inject

class AndroidTaskViewModel @Inject constructor(
    private val projectConfigProvider: ProjectConfigProvider,
    private val coursesRepository: CoursesRepository,
    private val taskManager: TaskManager,
    @AndroidCheckSystem private val checkSystem: AbstractCheckSystem,
    @Ui uiDispatcher: CoroutineDispatcher,
    @Io private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(uiDispatcher) {

    private val taskDescriptionMutable = MutableStateFlow<String?>(null)
    val taskDescription = taskDescriptionMutable.asStateFlow()

    private val isTestInProgressMutable = MutableStateFlow(false)
    val isTestInProgress = isTestInProgressMutable.asStateFlow()

    private val taskResultDataMutable = MutableStateFlow<TaskResultData?>(null)
    val taskResultData = taskResultDataMutable.asStateFlow()
    
    fun onViewCreated(courseId: String, taskId: String) {
        taskManager.getTaskDescription(TaskReference(courseId, taskId))
            .onEach { description -> taskDescriptionMutable.value = description }
            .launchIn(viewModelScope)
    }

    fun onTestClick() {
        taskResultDataMutable.value = null
        isTestInProgressMutable.value = true

        val rootDir = projectConfigProvider.rootDir ?: throw IllegalStateException("rootDir == null")
        // Сохранение всех открытых документов
        FileDocumentManager.getInstance().saveAllDocuments()
        VirtualFileManager.getInstance().asyncRefresh {
            viewModelScope.launch {
                withContext(ioDispatcher) {
                    val result = checkSystem.rutTests(File(rootDir))
                    taskResultDataMutable.value = TaskResultData(
                        result.result,
                        result.stdout,
                        result.stderr,
                    )
                    isTestInProgressMutable.value = false
                }
            }
        }
    }
}