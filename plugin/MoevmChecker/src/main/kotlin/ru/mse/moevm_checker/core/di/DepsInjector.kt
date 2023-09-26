package ru.mse.moevm_checker.core.di

import ru.mse.moevm_checker.core.checker.CoursesInfoValidatorForFile
import ru.mse.moevm_checker.core.checker.CoursesInfoValidatorForJson

object DepsInjector {
    private val coursesInfoValidatorForFile = CoursesInfoValidatorForJson()

    fun provideCoursesInfoValidatorForFile(): CoursesInfoValidatorForFile {
        return coursesInfoValidatorForFile
    }
}