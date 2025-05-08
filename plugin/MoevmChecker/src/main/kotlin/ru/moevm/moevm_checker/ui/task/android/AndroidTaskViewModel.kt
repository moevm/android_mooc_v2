package ru.moevm.moevm_checker.ui.task.android

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFileManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.tasks.TaskManager
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.tasks.TaskResultCodeEncoder
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskFactory
import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodeEnvironment
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
    @Ui uiDispatcher: CoroutineDispatcher,
    @Io private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(uiDispatcher) {

    private val isDescriptionLoadingMutable = MutableStateFlow(false)
    val isDescriptionLoading = isDescriptionLoadingMutable.asStateFlow()
    private val taskDescriptionMutable = MutableStateFlow<String?>(null)
    val taskDescription = taskDescriptionMutable.asStateFlow()

    private val isTestInProgressMutable = MutableStateFlow(false)
    val isTestInProgress = isTestInProgressMutable.asStateFlow()

    private val taskResultDataMutable = MutableStateFlow<TaskResultData?>(null)
    val taskResultData = taskResultDataMutable.asStateFlow()

    private lateinit var taskReference: TaskReference
    private val resultEncoder = TaskResultCodeEncoder()

    fun onViewCreated(courseId: String, taskId: String) {
        taskReference = TaskReference(courseId, taskId)
        isDescriptionLoadingMutable.value = true
        taskManager.getTaskDescription(taskReference)
            .onEach { description ->
                taskDescriptionMutable.value = description
                isDescriptionLoadingMutable.value = false
            }
            .launchIn(viewModelScope)
    }

    fun onTestClick() {
        taskResultDataMutable.value = null
        isTestInProgressMutable.value = true

        if (projectConfigProvider.rootDir == null) {
            taskResultDataMutable.value = TaskResultData(
                CheckResult.Error("Task folder didn't recognize"),
                "",
                "",
            )
            return
        }
        // Сохранение всех открытых документов
        FileDocumentManager.getInstance().saveAllDocuments()
        VirtualFileManager.getInstance().asyncRefresh {
            viewModelScope.launch {
                withContext(ioDispatcher) {
                    val taskDir = projectConfigProvider.rootDir
                    val taskData = coursesRepository.findTaskByReferenceFlow(taskReference).single()
                    if (taskDir == null || taskData == null) {
                        taskResultDataMutable.value = TaskResultData(
                            CheckResult.Error("Task folder didn't recognize"),
                            "",
                            "",
                        )
                    } else {
                        val codeTask = CodeTaskFactory.create(
                            TaskCodeEnvironment.Android(
                                File(taskDir),
                                projectConfigProvider.jdkPath
                            ), taskData.taskArgs
                        )
                        val codeTaskResult = codeTask.execute()
                        taskResultDataMutable.value = TaskResultData(
                            codeTaskResult.result,
                            codeTaskResult.stdout,
                            codeTaskResult.stderr,
                            codeTaskResult.probablyResultCode?.let {
                                resultEncoder.encode(
                                    taskReference,
                                    timestamp = System.currentTimeMillis(),
                                    codeTaskResult.probablyResultCode
                                )
                            }
                        )
                    }
                    isTestInProgressMutable.value = false
                }
            }
        }
    }
}