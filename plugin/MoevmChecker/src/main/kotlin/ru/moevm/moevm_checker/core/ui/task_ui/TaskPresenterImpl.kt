package ru.moevm.moevm_checker.core.ui.task_ui

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.moevm.moevm_checker.android.tasks.GradleCommandLine
import ru.moevm.moevm_checker.android.tasks.GradleOutput
import ru.moevm.moevm_checker.core.di.DepsInjector
import ru.moevm.moevm_checker.plugin_utils.catchLog

class TaskPresenterImpl(
    override val taskView: TaskView,
    private val ioDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().worker,
    private val uiDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().ui
) : TaskPresenter {
    private var prevTaskCheck: Job? = null

    private val environment = DepsInjector.projectEnvironmentInfo

    private val model: TaskModel = TaskModelImpl(this)

    override fun onViewCreated() {
        loadTaskProblem()
    }

    private fun loadTaskProblem() {
        flow {
            emit(model.getTaskDescriptionInfo())
        }
            .catchLog()
            .onEach { problem ->
                val htmlTaskProblemText = if (problem != null) {
                    convertMarkdownToHtml(problem)
                } else {
                    "error"
                }
                withContext(uiDispatcher) {
                    taskView.refreshUiState(
                        isLoadingLabelVisible = false,
                        htmlTaskProblemText = htmlTaskProblemText,
                        taskResultText = "",
                        taskStdoutText = "",
                        taskStderrText = ""
                    )
                }
            }
            .launchIn(CoroutineScope(ioDispatcher))
    }

    override fun onCheckClicked() {
        prevTaskCheck?.cancel()
        prevTaskCheck = CoroutineScope(ioDispatcher).launch {
            var output: GradleOutput? = null
            try {
                output = runTestTask()
            } catch (e: Exception) {
                output = GradleOutput(isSuccess = false, _messages = listOf(e.message ?: ""), "", "")
                println("exception: onCheckClicked ${e.message}")
            }

            val testResult = buildString {
                append("Result:\n")
                append(output?.firstMessage + "\n\n")
            }
            val testOutput = buildString {
                append("Stdout:\n")
                append(output?.stdout + "\n\n")
            }
            val testError = buildString {
                append("Stderr:\n")
                append(output?.stderr + "\n\n")
            }

            withContext(uiDispatcher) {
                taskView.refreshUiState(
                    isLoadingLabelVisible = false,
                    htmlTaskProblemText = convertMarkdownToHtml(model.taskProblemHtml), // FIXME Придумать сохранение получше
                    taskResultText = testResult,
                    taskStdoutText = testOutput,
                    taskStderrText = testError
                )
            }
        }
    }

    // FIXME Не отображает изображения
    private fun convertMarkdownToHtml(markdown: String): String {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
        val html = HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
        return html
    }

    private fun runTestTask(): GradleOutput? {
        val basePath = environment.rootDir
        val projectJdkPath = environment.jdkPath
        val gcl = GradleCommandLine.create(basePath, projectJdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }
}