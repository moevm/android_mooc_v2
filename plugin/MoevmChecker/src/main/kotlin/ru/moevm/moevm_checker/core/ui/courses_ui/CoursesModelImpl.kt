package ru.moevm.moevm_checker.core.ui.courses_ui

import ru.moevm.moevm_checker.core.data.CoursesInfo
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.file_system.repository.CoursesInfoRepository

class CoursesModelImpl(
    override val presenter: CoursesPresenter,
    private val coursesInfoRepository: CoursesInfoRepository = DepsInjector.provideCoursesInfoRepository()
) : CoursesModel {
    override fun forceInvalidateCoursesInfo(): CoursesInfo {
        val coursesInfo = coursesInfoRepository.invalidateCourseInfoState()
        val courses = requireNotNull(coursesInfo?.courses)
        val version = requireNotNull(coursesInfo?.version)
        return CoursesInfo(courses, version)
    }
}