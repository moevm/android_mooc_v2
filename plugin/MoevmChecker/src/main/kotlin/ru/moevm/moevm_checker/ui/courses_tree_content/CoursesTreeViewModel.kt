package ru.moevm.moevm_checker.ui.courses_tree_content

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.tasks.TaskFileManager
import ru.moevm.moevm_checker.dagger.Io
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.data.TaskVO
import javax.inject.Inject

class CoursesTreeViewModel @Inject constructor(
    private val taskFileManager: TaskFileManager,
    private val coursesRepository: CoursesRepository,
    @Ui private val uiDispatcher: CoroutineDispatcher,
    @Io private val ioDispatcher: CoroutineDispatcher,
): BaseViewModel(uiDispatcher) {

    private val listOfCoursesMutableState = MutableStateFlow<List<CourseVO>>(emptyList())
    val listOfCoursesState = listOfCoursesMutableState.asStateFlow()

    fun onViewCreated() {
        viewModelScope.launch {
            // TODO проверять версию репозитория
            val listOfCourses = coursesRepository.getCoursesInfo()
                .flowOn(ioDispatcher)
                .single().courses.map { course ->
                    CourseVO(
                        course.id,
                        course.name,
                        course.courseTasks
                            .map { task ->
                                TaskVO(task.id, task.name, task.type)
                            }
                    )
                }
            listOfCoursesMutableState.value = listOfCourses
        }
    }

    fun onDownloadTaskClick(taskId: String) {
        taskFileManager.downloadTaskFiles(taskId)
            .flowOn(ioDispatcher)
            .onEach {
                println("downloading task $taskId, status = $it")
            }
            .launchIn(viewModelScope)
    }

    fun onRemoveTaskClick(taskId: String) {
        taskFileManager.removeTaskFiles(taskId)
            .flowOn(ioDispatcher)
            .onEach {
                println("removing task $taskId, status = $it")
            }
            .launchIn(viewModelScope)
    }
}