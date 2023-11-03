package ru.moevm.moevm_checker.core.ui.courses_ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepository

class CoursesModelImpl(
    override val presenter: CoursesPresenter,
    private val coursesInfoRepository: CoursesInfoRepository = DepsInjector.provideCoursesInfoRepository()
) : CoursesModel {
    override fun forceInvalidateCoursesInfo(): Flow<CoursesInfo> {
        return coursesInfoRepository.invalidateCourseInfoState()
            .map { coursesInfo ->
                val courses = requireNotNull(coursesInfo?.courses)
                val version = requireNotNull(coursesInfo?.version)
                CoursesInfo(courses, version)
            }
    }
}