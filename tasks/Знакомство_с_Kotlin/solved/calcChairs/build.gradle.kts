plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/checker_lib-release.jar"))
//    implementation(project(":checker_lib"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.0")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("STANDARD_OUT")
    }
}
kotlin {
    jvmToolchain(17)
}