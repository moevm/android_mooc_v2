package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion

class AndroidLogcatCollector {
    var process: Process? = null

    fun start(selector: (String) -> Boolean) = flow {
        val processBuilder = ProcessBuilder("adb", "logcat")
        process = processBuilder.start()

        process?.inputStream?.bufferedReader().use { reader ->
            reader ?: return@use
            while (true) {
                val line = reader.readLine() ?: continue
                if (selector(line)) {
                    emit(line)
                }
            }
        }
    }.onCompletion {
        process?.destroy()
    }
}