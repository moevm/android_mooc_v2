package ru.moevm.moevm_checker.ui.courses_tree_content

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.tasks.TaskFileManager
import ru.moevm.moevm_checker.core.tasks.TaskManager
import ru.moevm.moevm_checker.core.utils.coroutine.EventSharedFlow
import ru.moevm.moevm_checker.dagger.Io
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.data.TaskVO
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.RootTreeNode
import javax.inject.Inject

class CoursesTreeViewModel @Inject constructor(
    private val taskManager: TaskManager,
    private val taskFileManager: TaskFileManager,
    private val coursesRepository: CoursesRepository,
    @Ui private val uiDispatcher: CoroutineDispatcher,
    @Io private val ioDispatcher: CoroutineDispatcher,
): BaseViewModel(uiDispatcher) {

    val coursesTreeModel = CoursesTreeModel(RootTreeNode.buildEmptyTree())

    private val shouldTreeInvalidateMutable = EventSharedFlow<Unit>()
    val shouldTreeInvalidate = shouldTreeInvalidateMutable.asSharedFlow()


    fun onViewCreated() {
        viewModelScope.launch {
            // TODO проверять версию репозитория
            val listOfCourses = coursesRepository.getCoursesInfoFlow()
                .flowOn(ioDispatcher)
                .single().courses.map { course ->
                    CourseVO(
                        course.id,
                        course.name,
                        course.courseTasks
                            .map { task ->
                                TaskVO(course.id, task.id, task.name, task.type)
                            }
                    )
                }
            withContext(uiDispatcher) {
                coursesTreeModel.updateTree(listOfCourses)
            }
            shouldTreeInvalidateMutable.emit(Unit)
        }
    }

    fun onOpenTaskClick(courseId: String, taskId: String) {
        taskManager.openTask(courseId, taskId)
            .flowOn(ioDispatcher)
            .onEach {
                println("opening task $taskId in course $courseId")
            }
            .launchIn(viewModelScope)
    }

    fun onDownloadTaskClick(courseId: String, taskId: String) {
        taskFileManager.downloadTaskFiles(courseId, taskId)
            .flowOn(ioDispatcher)
            .onEach {
                println("downloading task $taskId in course $courseId, status = $it")
            }
            .launchIn(viewModelScope)
    }

    fun onRemoveTaskClick(courseId: String, taskId: String) {
        taskFileManager.removeTaskFiles(courseId, taskId)
            .flowOn(ioDispatcher)
            .onEach {
                println("removing task $taskId in course $courseId, status = $it")
            }
            .launchIn(viewModelScope)
    }
}