package ru.moevm.moevm_checker.core.ui.courses_ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.moevm.moevm_checker.core.common.CoroutineDispatchers
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.ui.courses_ui.data.CourseVO
import ru.moevm.moevm_checker.core.ui.courses_ui.data.TaskVO

class CoursesPresenterImpl(
    override val coursesContentView: CoursesContentView,
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
                            course.tasks
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
}