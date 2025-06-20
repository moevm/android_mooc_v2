package ru.jengle88

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

private const val WELCOME = "" +
        "Привет! Я помогу составить тестовое окружение для задачи или очистить его для передачи студенту\n" +
        "Для этого меня нужно вместе с моей папкой переместить в проект, в котором нужно выполнить действие. Потом меня можно удалить, но уже вручную.\n" +
        "ВАЖНО! Я работаю только с kts синтаксисом для файлов сборки, а не Groovy\n" +
        "Что нужно сделать?"
private const val PROBLEM = "" +
        "Напиши число, соответствующее твоей задаче:\n" +
        "1. Подготовить окружение к написанию теста (если вы собираетесь написать тесты)\n" +
        "2. Убрать окружение для написания тестов (если вы собираетесь загрузить задачу студентам)\n" +
        "3. Посчитать SHA-256 хэш для тестовых файлов."
private const val LANGUAGE = "" +
        "Напиши число, соответствующее твоей платформе:\n" +
        "1. Android\n" +
        "2. Kotlin"
private const val WRONG_PARAMETER = "Неправильный параметр, попробуйте снова."
private const val CONTINUE = "Готово. Могу ещё чем-нибудь помочь? Напиши 'y', если да. Или что-нибудь другое, если нет."
private const val EXIT = "Всего доброго!"
private enum class ProblemType (val type: String) {
    PREPARE_ENV("1"),
    CLEAR_ENV("2"),
    GENERATE_SHA256("3"),
}
private enum class LanguageType (val type: String) {
    ANDROID("1"),
    KOTLIN("2"),
}

fun main(args: Array<String>) {
    println(WELCOME)
    do {
        println(PROBLEM)
        var problem = readln()
        while (problem !in ProblemType.entries.map { it.type }) {
            println(WRONG_PARAMETER)
            problem = readln()
        }
        println(LANGUAGE)
        var language = readln()
        while (language !in LanguageType.entries.map { it.type }) {
            println(WRONG_PARAMETER)
            language = readln()
        }
        try {
            startWizard(
                problem = requireNotNull(ProblemType.entries.find { it.type == problem }),
                language = requireNotNull(LanguageType.entries.find { it.type == language })
            )
        } catch (e: Exception) {
            println(e.message)
        }
        println(CONTINUE)
    } while (with(readln()) { this in "yYнН" })
    println(EXIT)
}

private fun startWizard(problem: ProblemType, language: LanguageType) {
    when {
        problem == ProblemType.PREPARE_ENV && language == LanguageType.ANDROID -> {
            prepareEnvForAndroid()
        }
        problem == ProblemType.CLEAR_ENV && language == LanguageType.ANDROID -> {
            clearEnvForAndroid()
        }
        problem == ProblemType.GENERATE_SHA256 && language == LanguageType.ANDROID -> {
            calcSHA256HashForAndroid()
        }
        problem == ProblemType.PREPARE_ENV && language == LanguageType.KOTLIN -> {
            prepareEnvForKotlin()
        }
        problem == ProblemType.CLEAR_ENV && language == LanguageType.KOTLIN -> {
            clearEnvForKotlin()
        }
        problem == ProblemType.GENERATE_SHA256 && language == LanguageType.KOTLIN -> {
            calcSHA256HashForKotlin()
        }
    }
}

private fun prepareEnvForAndroid() {
    // step 1 - копируем checker_lib
    val checkerLib = File(File(".", "prepareEnvForAndroid").path, "checker_lib")
    val checkerLibDestination = File("..", "checker_lib")
    checkerLib.copyRecursively(checkerLibDestination, overwrite = true)

    // step 2 - копируем libs
    val libs = File(File(".", "prepareEnvForAndroid").path, "libs")
    val libsDestination = File("..", "libs")
    libs.copyRecursively(libsDestination, overwrite = true)

    // step 3 - изменяем settings.gradle.kts
    val settingsGradleKts = File("..", "settings.gradle.kts")
    val settingsGradleKtsContent = settingsGradleKts.readText()
    settingsGradleKts.writeText(
        settingsGradleKtsContent.replace(
            "include(\":app\")",
            """
                include(":app")
                include(":checker_lib")
                include(":libs")
            """.trimIndent()
        )
    )

    // step 4 - изменяем build.gradle.kts
    val buildGradleKts = File("..", "/app/build.gradle.kts")
    val buildGradleKtsContent = buildGradleKts.readText()
    buildGradleKts.writeText(
        buildGradleKtsContent.replace(
            "dependencies {",
            """
                dependencies {
                    // *** do not remove this dependencies
                    // implementation(fileTree("libs") { include("*.jar") })
                    // androidTestImplementation(project(":libs"))
                    androidTestImplementation(project(":checker_lib"))
                    // ***
            """.trimIndent()
        )
    )
}

private fun clearEnvForAndroid() {
    // step 1 - удаляем checker_lib
    val checkerLib = File("..", "checker_lib")
    checkerLib.deleteRecursively()

    // step 2 - изменяем settings.gradle.kts
    val settingsGradleKts = File("..", "settings.gradle.kts")
    val settingsGradleKtsContent = settingsGradleKts.readText()
    settingsGradleKts.writeText(
        settingsGradleKtsContent
            .replaceAll(
                mapOf(
                    "// include(\":checker_lib\")" to "",
                    "//include(\":checker_lib\")" to "",
                    "include(\":checker_lib\")" to ""
                )
            )
    )

    // step 3 - изменяем build.gradle.kts
    val buildGradleKts = File("..", "/app/build.gradle.kts")
    val buildGradleKtsContent = buildGradleKts.readText()
    buildGradleKts.writeText(
        buildGradleKtsContent
            .replaceAll(
                mapOf(
                    "// androidTestImplementation(project(\":checker_lib\"))" to "",
                    "//androidTestImplementation(project(\":checker_lib\"))" to "",
                    "androidTestImplementation(project(\":checker_lib\"))" to "",

                    "// implementation(fileTree(\"libs\") { include(\"*.jar\") })" to "implementation(fileTree(\"libs\") { include(\"*.jar\") })",
                    "//implementation(fileTree(\"libs\") { include(\"*.jar\") })" to "implementation(fileTree(\"libs\") { include(\"*.jar\") })",

                    "// androidTestImplementation(project(\":libs\"))" to "androidTestImplementation(project(\":libs\"))",
                    "//androidTestImplementation(project(\":libs\"))" to "androidTestImplementation(project(\":libs\"))",
                )
            )
    )

    // step 4 - удаляем кэш сборки и локальные файлы
    File("..", "/app/build").deleteRecursively()
    File("..", "/build").deleteRecursively()
    File("..", ".gradle").deleteRecursively()
    File("..", ".kotlin").deleteRecursively()
    File("..", ".idea").deleteRecursively()
    File("..", "local.properties").delete()
}

private fun calcSHA256HashForAndroid() {
    val checkerLibFile = File(buildFilePath("..", "libs", "checker_lib-release.aar"))
    val instrumentalTestFile = tryGetTestFile(File(buildFilePath("..", "app", "src", "androidTest", "java")), "ExampleInstrumentedTest.kt")
    val unitTestFile = tryGetTestFile(File(buildFilePath("..", "app", "src", "test", "java")), "ExampleUnitTest.kt")
    val result = buildList {
        println("Check checkerLib ${checkerLibFile.absolutePath}")
        if (checkerLibFile.exists() && checkerLibFile.isFile) {
            add("test_archive: ${computeHashCode(checkerLibFile)}")
        }
        println("Check instrumental-test ${instrumentalTestFile?.absolutePath}")
        if (instrumentalTestFile != null && instrumentalTestFile.exists() && instrumentalTestFile.isFile) {
            add("test_instrumental_launcher: ${computeHashCode(instrumentalTestFile)}")
        }
        println("Check unit-test ${unitTestFile?.absolutePath}")
        if (unitTestFile != null && unitTestFile.exists() && unitTestFile.isFile) {
            add("test_unit_launcher: ${computeHashCode(unitTestFile)}")
        }
    }
    println(result.joinToString("\n"))
}

private fun prepareEnvForKotlin() {
    // step 1 - копируем checker_lib
    val checkerLib = File(File(".", "prepareEnvForKotlin").path, "checker_lib")
    val checkerLibDestination = File("..", "checker_lib")
    checkerLib.copyRecursively(checkerLibDestination, overwrite = true)

    // step 2 - копируем libs
    val libs = File(File(".", "prepareEnvForKotlin").path, "libs")
    val libsDestination = File("..", "libs")
    libs.copyRecursively(libsDestination, overwrite = true)

    // step 3 - изменяем settings.gradle.kts
    val settingsGradleKts = File("..", "settings.gradle.kts")
    val settingsGradleKtsContent = settingsGradleKts.readText()
    settingsGradleKts.writeText(
        buildString {
            append(settingsGradleKtsContent)
            appendLine()
            appendLine("include(\":checker_lib\")")
            appendLine("include(\":libs\")")
        }
    )

    // step 4 - изменяем build.gradle.kts
    val buildGradleKts = File("..", "build.gradle.kts")
    val buildGradleKtsContent = buildGradleKts.readText()
    buildGradleKts.writeText(
        buildGradleKtsContent.replace(
            "dependencies {",
            """
                dependencies {
                    // *** do not remove this dependencies
                    // implementation(fileTree("libs") { include("*.jar") })
                    // testImplementation(project(":libs"))
                    testImplementation(project(":checker_lib"))
                    // ***
            """.trimIndent()
        )
    )
}

private fun clearEnvForKotlin() {
    // step 1 - удаляем checker_lib
    val checkerLib = File("..", "checker_lib")
    checkerLib.deleteRecursively()

    // step 2 - изменяем settings.gradle.kts
    val settingsGradleKts = File("..", "settings.gradle.kts")
    val settingsGradleKtsContent = settingsGradleKts.readText()
    settingsGradleKts.writeText(
        settingsGradleKtsContent
            .replaceAll(
                mapOf(
                    "//include(\":checker_lib\")" to "",
                    "// include(\":checker_lib\")" to "",
                    "include(\":checker_lib\")" to ""
                )
            )
    )

    // step 3 - изменяем build.gradle.kts
    val buildGradleKts = File("..", "build.gradle.kts")
    val buildGradleKtsContent = buildGradleKts.readText()
    buildGradleKts.writeText(
        buildGradleKtsContent
            .replaceAll(
                mapOf(
                    "// testImplementation(project(\":checker_lib\"))" to "",
                    "//testImplementation(project(\":checker_lib\"))" to "",
                    "testImplementation(project(\":checker_lib\"))" to "",

                    "// implementation(fileTree(\"libs\") { include(\"*.jar\") })" to "implementation(fileTree(\"libs\") { include(\"*.jar\") })",
                    "//implementation(fileTree(\"libs\") { include(\"*.jar\") })" to "implementation(fileTree(\"libs\") { include(\"*.jar\") })",

                    "// testImplementation(project(\":libs\"))" to "testImplementation(project(\":libs\"))",
                    "//testImplementation(project(\":libs\"))" to "testImplementation(project(\":libs\"))",
                )
            )
    )

    // step 4 - удаляем кэш сборки и локальные файлы
    File("..", "build").deleteRecursively()
    File("..", ".gradle").deleteRecursively()
    File("..", ".kotlin").deleteRecursively()
    File("..", ".idea").deleteRecursively()
    File("..", "local.properties").delete()
}

private fun calcSHA256HashForKotlin() {
    val checkerLibFile = File(buildFilePath("..", "libs", "checker_lib-release.jar"))
    val unitTestFile = File(buildFilePath("..", "src", "test", "kotlin", "Test.kt"))
    val result = buildList {
        println("Check checkerLib ${checkerLibFile.absolutePath}")
        if (checkerLibFile.exists() && checkerLibFile.isFile) {
            add("test_archive: ${computeHashCode(checkerLibFile)}")
        }
        println("Check unit-test ${unitTestFile.absolutePath}")
        if (unitTestFile.exists() && unitTestFile.isFile) {
            add("test_unit_launcher: ${computeHashCode(unitTestFile)}")
        }
    }
    println(result.joinToString("\n"))
}

private fun String.replaceAll(mapOfReplacing: Map<String, String>): String {
    return mapOfReplacing.entries.fold(this) { acc, s ->
        acc.replace(s.key, s.value)
    }
}

private fun computeHashCode(file: File): String {
    val algorithm = "SHA-256"
    val digest = MessageDigest.getInstance(algorithm)
    FileInputStream(file).use { fis ->
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

private fun tryGetTestFile(basePathToTest: File, endpointFileName: String): File? {
    basePathToTest.walkTopDown().forEach { file ->
        if (file.isFile && file.name == endpointFileName) {
            return file
        }
    }
    return null
}

private fun buildFilePath(vararg pieceOfPath: String): String {
    return pieceOfPath.fold(".") { prev, new ->
        File(prev, new).path
    }
}

