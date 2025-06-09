package ru.moevm.moevm_checker.ui.task.android

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFileManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.ProjectConfigProvider
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.filesystem.HashFileVerificator
import ru.moevm.moevm_checker.core.tasks.TaskReference
import ru.moevm.moevm_checker.core.tasks.TaskResultCodeEncoder
import ru.moevm.moevm_checker.core.tasks.codetask.CheckResult
import ru.moevm.moevm_checker.core.tasks.codetask.CodeTaskFactory
import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodeEnvironment
import ru.moevm.moevm_checker.core.tasks.codetask.TaskCodePlatform
import ru.moevm.moevm_checker.core.utils.coroutine.catchWithLog
import ru.moevm.moevm_checker.dagger.Io
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import ru.moevm.moevm_checker.ui.task.TaskResultData
import ru.moevm.moevm_checker.utils.PluginLogger
import java.io.File
import javax.inject.Inject

class AndroidTaskViewModel @Inject constructor(
    private val projectConfigProvider: ProjectConfigProvider,
    private val coursesRepository: CoursesRepository,
    private val hashFileVerificator: HashFileVerificator,
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

    fun onViewCreated(taskReference: TaskReference) {
        this.taskReference = taskReference
        PluginLogger.d("TaskViewModel", "onViewCreated")
        isDescriptionLoadingMutable.value = true
        coursesRepository.getTaskDescriptionFlow(taskReference)
            .flowOn(ioDispatcher)
            .catchWithLog { error ->
                PluginLogger.d("TaskViewModel", "task with $taskReference description update with fail: $error")
                taskDescriptionMutable.value = "Не удалось загрузить данные"
                isDescriptionLoadingMutable.value = false
            }
            .onEach { description ->
                PluginLogger.d("TaskViewModel", "task $taskReference description loaded")
                taskDescriptionMutable.value = description
                isDescriptionLoadingMutable.value = false
            }
            .launchIn(viewModelScope)
    }

    fun onTestClick() {
        PluginLogger.d("TaskViewModel", "onTestClick: $taskReference")
        taskResultDataMutable.value = null
        isTestInProgressMutable.value = true

        if (projectConfigProvider.rootDir == null) {
            PluginLogger.d("TaskViewModel", "onTestClick task root directory not set")
            taskResultDataMutable.value = TaskResultData(
                CheckResult.Error("Task folder didn't recognize"),
                "",
                "",
            )
            return
        }
        // Сохранение всех открытых документов
        FileDocumentManager.getInstance().saveAllDocuments()
        PluginLogger.d("TaskViewModel", "onTestClick saveAllDocuments")

        VirtualFileManager.getInstance().asyncRefresh {
            viewModelScope.launch(ioDispatcher) {
                PluginLogger.d("TaskViewModel", "onTestClick start opening task")
                val taskDir = projectConfigProvider.rootDir
                val taskData = coursesRepository.findTaskByReferenceFlow(taskReference).single()
                if (taskDir == null || taskData == null) {
                    PluginLogger.d("TaskViewModel", "onTestClick taskDir or taskData is null")
                    taskResultDataMutable.value = TaskResultData(
                        CheckResult.Error("Task folder didn't recognize"),
                        "",
                        "",
                    )
                } else {
                    PluginLogger.d("TaskViewModel", "onTestClick startTask")
                    startTask(taskDir, taskData)
                }
                isTestInProgressMutable.value = false
            }
        }
    }

    private fun startTask(taskDir: String, taskData: CourseTask) {
        val environment = when (taskData.courseTaskPlatform) {
            TaskCodePlatform.ANDROID.type -> {
                PluginLogger.d("TaskViewModel", "startTask android")
                TaskCodeEnvironment.Android(File(taskDir), projectConfigProvider.jdkPath)
            }
            TaskCodePlatform.KOTLIN.type -> {
                PluginLogger.d("TaskViewModel", "startTask kotlin")
                TaskCodeEnvironment.Kotlin(File(taskDir), projectConfigProvider.jdkPath)
            }
            else -> {
                return
            }
        }
        val codeTask = CodeTaskFactory.create(
            environment,
            taskData.taskArgs,
            hashFileVerificator,
            taskData.taskFileHashes,
        )
        PluginLogger.d("TaskViewModel", "startTask codeTask created")
        val codeTaskResult = codeTask.execute()
        PluginLogger.d("TaskViewModel", "startTask codeTask finished with codeTaskResult")
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
}