import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


val localProperties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}
val kakaoApiKey: String = localProperties["KAKAO_MAP_KEY"] as String

android {
    namespace = "com.example.parkinglot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.parkinglot"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 👇 여기에 KAKAO_MAP_KEY 전달
        manifestPlaceholders["KAKAO_MAP_KEY"] = kakaoApiKey
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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- 추가 의존성 시작 ---

    // Kakao Map SDK (v2 기준)
    implementation("com.kakao.sdk:v2-map:2.15.0") // 최신 버전 확인 필요

    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Google Play Services - Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // 권한 요청 (Accompanist)
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // --- 테스트 및 디버그 ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
