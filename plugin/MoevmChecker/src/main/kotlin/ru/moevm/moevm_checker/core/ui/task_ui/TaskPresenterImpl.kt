package ru.moevm.moevm_checker.core.ui.task_ui

import kotlinx.coroutines.*
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.moevm.moevm_checker.android.tasks.GradleCommandLine
import ru.moevm.moevm_checker.android.tasks.GradleOutput
import ru.moevm.moevm_checker.core.di.DepsInjector

class TaskPresenterImpl(
    override val taskView: TaskView,
    private val ioDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().worker,
    private val uiDispatcher: CoroutineDispatcher = DepsInjector.provideDispatcher().ui
) : TaskPresenter {
    private var prevTaskCheck: Job? = null

    private val environment = DepsInjector.projectEnvironmentInfo

    private val simpleMarkdown = "# Здесь будет описание задачи \n" +
            "## Здесь будет описание задачи \n" +
            "# Здесь будет описание задачи \n" +
            "## Здесь будет описание задачи \n" +
            "### Наша задача очень крутая!"
    override fun onViewCreated() {
        val htmlTaskProblemText = convertMarkdownToHtml()
        taskView.refreshUiState(
            isLoadingLabelVisible = false,
            htmlTaskProblemText = htmlTaskProblemText,
            taskResultText = "",
            taskStdoutText = "",
            taskStderrText = ""
        )
    }

    override fun onCheckClicked() {
        prevTaskCheck?.cancel()
        prevTaskCheck = CoroutineScope(ioDispatcher).launch {
            val output = runTestTask()
            val htmlTaskProblemText = convertMarkdownToHtml()
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
                    htmlTaskProblemText = htmlTaskProblemText,
                    taskResultText = testResult,
                    taskStdoutText = testOutput,
                    taskStderrText = testError
                )
            }
        }
    }

    // FIXME Не отображает изображения
    private fun convertMarkdownToHtml(): String {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(simpleMarkdown)
        val html = HtmlGenerator(simpleMarkdown, parsedTree, flavour).generateHtml()
        return html
    }

    private fun runTestTask(): GradleOutput? {
        val basePath = environment.rootDir
        val projectJdkPath = environment.jdkPath
        val gcl = GradleCommandLine.create(basePath, projectJdkPath, "app:connectedDebugAndroidTest")
        return gcl.launch()
    }
}