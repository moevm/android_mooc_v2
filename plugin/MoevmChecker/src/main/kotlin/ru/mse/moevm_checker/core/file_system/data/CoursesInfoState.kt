package ru.mse.moevm_checker.core.file_system.data

import ru.mse.moevm_checker.core.data.CoursesInfo

sealed class CoursesInfoState(val coursesInfo: CoursesInfo?) {
    object NoData : CoursesInfoState(null)

    class ActualData(coursesInfo: CoursesInfo) : CoursesInfoState(coursesInfo)

    class OutdatedData(coursesInfo: CoursesInfo) : CoursesInfoState(coursesInfo)
}