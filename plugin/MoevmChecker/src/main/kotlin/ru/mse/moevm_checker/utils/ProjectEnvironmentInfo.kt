package ru.mse.moevm_checker.utils

object ProjectEnvironmentInfo {
    lateinit var rootDir: String

    fun init(
        rootDir: String
    ) {
        this.rootDir = rootDir
    }
}