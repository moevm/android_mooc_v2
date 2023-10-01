package ru.mse.moevm_checker.core.checker

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class CoursesInfoValidatorForJson : CoursesInfoValidatorForFile {
    private val isValidPlaceMutableState = MutableStateFlow(ValidPlaceStatus.NO_INFO)
    override val isValidPlaceState: StateFlow<ValidPlaceStatus> = isValidPlaceMutableState.asStateFlow()

    private val fileName = "moevm_checker.json"

    override fun updateIsValidFileState(coursesPlacePath: String) {
        val file = File(coursesPlacePath, fileName)
        if (isValidFile(file)) {
            isValidPlaceMutableState.value = ValidPlaceStatus.VALID
        } else {
            isValidPlaceMutableState.value = ValidPlaceStatus.INVALID
        }
    }

    private fun isValidFile(file: File): Boolean {
        return file.exists() && isValidFileContent(file)
    }

    private fun isValidFileContent(file: File): Boolean {
        // TODO: Добавить реализацию проверки файла
        return true
    }
}