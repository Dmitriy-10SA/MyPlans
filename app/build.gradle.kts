plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("kotlin-kapt")
}

android {
    namespace = "com.andef.myplans"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.andef.myplans"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "5.0"

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.google.dagger:dagger:2.55")
    kapt("com.google.dagger:dagger-compiler:2.55")
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