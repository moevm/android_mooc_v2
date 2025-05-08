package ru.moevm.moevm_checker.core.tasks.codetask.platforms.android

import kotlinx.coroutines.*

class LogcatCollector {
    var job: Job? = null
    var data: MutableList<String> = mutableListOf()

    fun start(selector: (String) -> Boolean, dispatcher: CoroutineDispatcher) {
        job?.cancel()
        job = CoroutineScope(dispatcher + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace() // Базовый обработчик исключений
        }).launch {
            val processBuilder = ProcessBuilder("adb", "logcat")
            val process = processBuilder.start()

            process.inputStream.bufferedReader().use { reader ->
                while (isActive) {
                    val line = reader.readLine() ?: break
                    if (selector(line)) {
                        data.add(line)
                    }
                }
            }
        }
    }

    fun collect(): List<String> {
        job?.cancel()
        return data
    }
}