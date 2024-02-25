package ru.moevm.moevm_checker.core.ui.courses_ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.common.CoroutineDispatchers
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.task.TaskFileManager
import ru.moevm.moevm_checker.core.task.TaskManager
import ru.moevm.moevm_checker.core.ui.courses_ui.data.CourseVO
import ru.moevm.moevm_checker.core.ui.courses_ui.data.TaskVO

class CoursesPresenterImpl(
    override val coursesContentView: CoursesContentView,
    private val taskManager: TaskManager = DepsInjector.provideTaskManager(),
    private val taskFileManager: TaskFileManager = DepsInjector.provideTaskFileManager(),
    private val dispatchers: CoroutineDispatchers = DepsInjector.provideDispatcher()
) : CoursesPresenter {
    override val coursesModel = CoursesModelImpl(this)

    override fun onCoursesContentViewCreated() {
        onRefreshCoursesInfo()
    }

    override fun onRefreshCoursesInfo() {
        CoroutineScope(dispatchers.worker).launch {
            coursesModel.forceInvalidateCoursesInfo()
                .map { courseInfo ->
                    courseInfo.courses.map { course ->
                        CourseVO(
                            course.id,
                            course.name,
                            course.courseTasks
                                .map { task ->
                                    TaskVO(task.id, task.name, task.type)
                                }
                        )
                    }
                }
                .collect { courses ->
                    withContext(dispatchers.ui) {
                        coursesContentView.refreshUiState(courses)
                    }
                }
        }
    }

    override fun onOpenTaskClick(taskId: String) {
        taskManager.setCurrentTask(taskId)
    }

    override fun onDownloadTaskClick(taskId: String) {
        taskFileManager.downloadTaskFiles(taskId)
            ?.onEach {
                println(it.name)
            }
            ?.launchIn(CoroutineScope(dispatchers.worker))
    }

    override fun onRemoveTaskClick(taskId: String) {
        taskFileManager.removeTaskFiles(taskId)
    }
}