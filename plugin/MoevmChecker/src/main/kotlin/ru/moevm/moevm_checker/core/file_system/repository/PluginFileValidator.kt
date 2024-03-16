package ru.moevm.moevm_checker.core.file_system.repository

interface PluginFileValidator {
    fun isMainCoursesFileValid(): Boolean

    fun isTaskFileValid(): Boolean
}