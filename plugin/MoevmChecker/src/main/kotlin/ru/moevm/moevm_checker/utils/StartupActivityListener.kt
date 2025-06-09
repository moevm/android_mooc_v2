package ru.moevm.moevm_checker.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager

class StartupActivityListener : StartupActivity.DumbAware {

    override fun runActivity(project: Project) {
        prepareProjectEnvironment(project)
    }

    private fun prepareProjectEnvironment(
        project: Project
    ) {
        // sync project
        VirtualFileManager.getInstance().asyncRefresh {
            project.guessProjectDir()?.refresh(false, true)
            val path = project.guessProjectDir()?.path
            if (path != null) {
                startPluginLogger(path)
                PluginLogger.d("StartupActivityListener", "Project root directory: $path")

            } else {
                PluginLogger.d("StartupActivityListener", "Cannot find project for $path")
            }
        }
    }

    private fun startPluginLogger(rootDir: String) {
        PluginLogger.setFile(rootDir)
    }
}
