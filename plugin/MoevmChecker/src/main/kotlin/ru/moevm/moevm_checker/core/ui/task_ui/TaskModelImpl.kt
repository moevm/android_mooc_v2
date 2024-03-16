package ru.moevm.moevm_checker.core.ui.task_ui

import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.core.file_system.reader.CoursesInfoReader
import ru.moevm.moevm_checker.core.task.Task
import ru.moevm.moevm_checker.utils.ProjectEnvironmentInfo
import java.io.File

class TaskModelImpl(
    override val presenter: TaskPresenter,
    private val coursesInfoReader: CoursesInfoReader = DepsInjector.provideCoursesInfoReader(),
    projectEnvironmentInfo: ProjectEnvironmentInfo = DepsInjector.projectEnvironmentInfo,
) : TaskModel {
    override val task: Task? = coursesInfoReader.readTask(File(projectEnvironmentInfo.rootDir))
    override var taskProblemHtml: String = ""

    override fun getTaskDescriptionInfo(): String? {
        if (task?.pathToTask?.isEmpty() != false) {
            return null
        }
        return coursesInfoReader.readTaskDescription(File(task.pathToTask))?.apply {
            taskProblemHtml = this
        }
    }
}