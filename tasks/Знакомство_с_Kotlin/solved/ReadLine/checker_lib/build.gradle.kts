plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.example"
version = "release"

repositories {
    mavenCentral()
}

dependencies {
    implementation("junit:junit:4.13.2")
    implementation("org.junit.vintage:junit-vintage-engine:5.10.0")
}

tasks {
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

    register<Jar>("release") {
        archiveBaseName.set("checker_lib")
        from(sourceSets.main.get().output)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
    }
}
kotlin {
    jvmToolchain(17)
}