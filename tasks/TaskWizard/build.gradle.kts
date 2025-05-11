plugins {
    kotlin("jvm") version "2.1.20"
}

group = "ru.jengle88"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.jar.configure {
    manifest {
        attributes(mapOf("Main-Class" to "ru.jengle88.MainKt"))
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}