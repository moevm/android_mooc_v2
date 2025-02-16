package ru.moevm.moevm_checker.ui.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.tasks.TaskManager
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import javax.inject.Inject

class TaskViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val taskManager: TaskManager,
    @Ui uiDispatcher: CoroutineDispatcher,
) : BaseViewModel(uiDispatcher) {

    private val taskDescriptionMutable = MutableStateFlow<String?>(null)
    val taskDescription = taskDescriptionMutable.asStateFlow()

    private val isTestInProgressMutable = MutableStateFlow(false)
    val isTestInProgress = isTestInProgressMutable.asStateFlow()

    private val taskResultDataMutable = MutableStateFlow<TaskResultData?>(null)
    val taskResultData = taskResultDataMutable.asStateFlow()
    
    private val courseTaskMutable = MutableStateFlow<CourseTask?>(null)
    val courseTask = courseTaskMutable.asStateFlow()

    fun onViewCreated(courseId: String, taskId: String) {
        coursesRepository.getTaskInfo(courseId, taskId)
            .onEach { task -> courseTaskMutable.value = task }
            .launchIn(viewModelScope)

        taskManager.getTaskDescription(taskId)
            .onEach { description -> taskDescriptionMutable.value = description }
            .launchIn(viewModelScope)
    }

    fun onTestClick() {
        taskResultDataMutable.value = null
        isTestInProgressMutable.value = true

        // TODO Сделать запуск теста
        isTestInProgressMutable.value = false
    }
}