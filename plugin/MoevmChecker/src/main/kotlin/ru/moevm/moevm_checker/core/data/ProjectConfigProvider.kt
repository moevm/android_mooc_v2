package ru.moevm.moevm_checker.core.data

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager

class ProjectConfigProvider(
    private val project: Project
) {
    val rootDir: String?
        get() = project.guessProjectDir()?.path

    val jdkPath: String?
        get() = ProjectRootManager.getInstance(project).projectSdk?.homePath
}
