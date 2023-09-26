package ru.mse.moevm_checker.core.ui.splashWindow

import com.intellij.execution.Executor
import com.intellij.execution.processTools.getResultStdoutStr
import com.intellij.ide.ui.laf.darcula.ui.DarculaProgressBarBorder
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.components.ProgressBarLoadingDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.cli.CommandLine
import ru.mse.moevm_checker.core.ui.BaseWindow
import ru.mse.moevm_checker.utils.ResStr
import java.awt.Desktop
import java.io.InputStreamReader
import java.lang.reflect.Executable
import java.util.concurrent.Executors
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JProgressBar

class SplashWindow(private val projectPath: String) : BaseWindow {
    lateinit var label: JLabel
    var cnt = 1

    override fun getContent(): DialogPanel {
        return panel {
            panel {
                row {
                    label(ResStr.getString("splashWindowMainTitle"))
                }
                row {
                    icon(AnimatedIcon.Default())
                    label(ResStr.getString("splashWindowMainLoaderTitle"))
                }
                row {
                    button("Click me") {
                        CoroutineScope(Dispatchers.Default).launch {
                            val process = ProcessBuilder(listOf("echo", "Привет всем ${cnt++} раз"))
                            val output = process.start().getResultStdoutStr().getOrDefault("error")
                            label.text = output
                        }
                    }
                }
                row {
                    label = label("").component
                }
            }
                .align(Align.CENTER)
        }
    }
}