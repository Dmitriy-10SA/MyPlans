plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

android {
    namespace = "com.andef.myplans"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.andef.myplans"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.constraintlayout)
    ksp("androidx.room:room-compiler:2.6.1")
    implementation(libs.androidx.room.rxjava2)
    implementation("com.applandeo:material-calendar-view:1.9.2")
    implementation(libs.rxandroid)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
}