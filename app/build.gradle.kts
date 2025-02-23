import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt)        // Dagger-Hilt for dependency injection
    alias(libs.plugins.kotlin.kapt)
}

val localPropertiesFile = rootProject.file("local.properties")
val properties = Properties()
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { properties.load(it) }
}

android {
    namespace = "io.hangout.guide"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.hangout.guide"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GOOGLE_GEMINI_API_KEY", "\"${properties["GOOGLE_GEMINI_API_KEY"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}

dependencies {

    implementation (libs.androidx.navigation.compose)
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // DataStore Preferences
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)

    // Google Maps SDK for Android
    implementation(libs.places)
    implementation(libs.play.services.maps)

    // Google maps Compose
    implementation(libs.maps.compose)

    // Logging
    implementation(libs.timber)

    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")

    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    // Generative AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("dev.shreyaspatil.permission-flow:permission-flow-compose:1.2.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.24.13-rc")
    implementation("com.google.android.gms:play-services-location:21.1.0")
}