plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.checker_lib"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("junit:junit:4.13.2")
    implementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.kaspersky.android-components:kaspresso:1.5.3")
    implementation("androidx.test.ext:junit:1.1.4")
    implementation( "androidx.test.ext:junit-ktx:1.1.4")
    implementation("androidx.test:rules:1.5.0")
}