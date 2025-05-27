package ru.moevm.moevm_checker.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class AppShutdownListener : ProjectManagerListener {

    override fun projectClosingBeforeSave(project: Project) {
        PluginLogger.close()
        super.projectClosingBeforeSave(project)
    }
}