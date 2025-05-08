plugins {
    kotlin("jvm") version "2.1.10"
}

group = "ru.jengle88"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("junit:junit:4.13.2") // Последняя стабильная версия JUnit 4
    implementation("org.junit.vintage:junit-vintage-engine:5.10.0") // Для запуска JUnit 4 тестов
}

tasks {
    // Обязательно включите поддержку обфускации
    withType<JavaExec> {
        val proguardConfigFile = file("proguard-rules.pro")
        doFirst {
            if (!proguardConfigFile.exists()) {
                throw GradleException("ProGuard configuration file not found: $proguardConfigFile")
            }
        }
        jvmArgs = listOf(
            "-javaagent:${project.rootDir.absolutePath}/lib/proguard.jar"
        )
    }

    // Добавляем задачу для генерации jar файла
    register<Jar>("createJar") {
        archiveBaseName.set("kt_test") // Название итогового файла
        from(sourceSets.main.get().output) // Исходники итогового JAR
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}