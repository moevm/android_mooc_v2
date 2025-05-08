pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.intellij.platform").version(extra["intellij.version"] as String)
        id("com.google.devtools.ksp").version(extra["devtools.ksp"] as String)
    }
}

rootProject.name = "moevm_checker"