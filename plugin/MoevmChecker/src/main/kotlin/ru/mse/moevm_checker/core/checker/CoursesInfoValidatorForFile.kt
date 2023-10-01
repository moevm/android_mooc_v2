package ru.mse.moevm_checker.core.checker

import kotlinx.coroutines.flow.StateFlow

interface CoursesInfoValidatorForFile {
    val isValidPlaceState: StateFlow<ValidPlaceStatus>

    fun updateIsValidFileState(coursesPlacePath: String)
}