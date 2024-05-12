plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")

//    id("com.google.devtools.ksp")
}

android {
    namespace = "com.vag.lmsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vag.lmsapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        this.dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    kapt(libs.androidx.annotation)

    implementation(libs.escpos.thermalprinter.android)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.pattern.lock)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    implementation(libs.photoview)

    implementation(libs.androidx.databinding.runtime)
    kapt(libs.androidx.databinding.compiler)

    implementation(libs.androidx.fragment.ktx)

    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.play.services.location)

    implementation("com.google.zxing:core:3.3.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.4.0")
}