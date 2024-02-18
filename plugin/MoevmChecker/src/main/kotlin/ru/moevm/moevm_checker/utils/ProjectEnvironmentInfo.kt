package ru.moevm.moevm_checker.utils

class ProjectEnvironmentInfo {
    lateinit var rootDir: String
    lateinit var jdkPath: String

    fun init(
        rootDir: String,
        jdkPath: String
    ) {
        this.rootDir = rootDir
        this.jdkPath = jdkPath
    }
}