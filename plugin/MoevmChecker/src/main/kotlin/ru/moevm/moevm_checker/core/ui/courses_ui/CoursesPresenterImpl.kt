package ru.moevm.moevm_checker.core.ui.courses_ui

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
        // TODO Переделать на flow
        val coursesInfo = coursesModel.forceInvalidateCoursesInfo()
        val courses = coursesInfo.courses.map { course ->
            CourseVO(
                course.id,
                course.name,
                course.courseTasks
                    .map { task ->
                        TaskVO(task.id, task.name, task.type)
                    }
            )
        }
        coursesContentView.refreshUiState(courses)
    }
}