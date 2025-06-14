// app/build.gradle.kts
import org.gradle.kotlin.dsl.implementation // 이 import는 그대로 유지합니다.

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Compose 플러그인
    alias(libs.plugins.secrets.gradle.plugin) // Secrets Gradle Plugin
}

android {
    namespace = "com.example.parkinglot"
    compileSdk = 35 // libs.versions.toml에 정의된 agp 버전에 맞춤

    defaultConfig {
        applicationId = "com.example.parkinglot"
        minSdk = 34 // Kakao Vector Map SDK는 minSdk 24 이상을 권장합니다.
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Secrets Gradle Plugin을 사용하면 secrets.properties 파일에 정의된 키가
        // 자동으로 BuildConfig에 필드로 생성됩니다.
        // 따라서 여기서는 buildConfigField를 사용하여 API 키를 직접 정의할 필요가 없습니다.
        // KAKAO_REST_KEY, SEOUL_API_KEY 등은 BuildConfig.KAKAO_REST_KEY 형태로 직접 접근 가능합니다.
        // 이전에 중복되거나 잘못된 접근이었던 buildConfigField 라인들을 모두 제거합니다.
        buildConfigField("String", "KAKAO_MAP_KEY", "\"${properties["KAKAO_MAP_KEY"]}\"")
        buildConfigField("String", "SEOUL_API_KEY", "\"${properties["SEOUL_API_KEY"]}\"")
        buildConfigField("String", "KAKAO_REST_KEY", "\"${properties["KAKAO_REST_KEY"]}\"")
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
        buildConfig = true // BuildConfig 생성을 위해 필요
    }
}

dependencies {
    // AndroidX & Compose 기본 의존성
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel Compose 의존성

    // Kakao Map SDK (Kakao Vector Map SDK)
    implementation(libs.kakao.maps) // 이 의존성만 사용합니다.

    // 위치 정보 및 권한 (accompanist, play-services-location)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kakao.maps)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)

    // Retrofit2 (버전 2.9.0 유지)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // 중복 제거 후 하나만 유지

    // XML 응답을 처리하기 위한 SimpleXML (버전 유지)
    // OkHttp 버전을 logging-interceptor와 동일하게 맞춥니다.
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // 4.9.3 -> 4.12.0
    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")

    // OkHttp 로깅 인터셉터 (버전 유지)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // libDaumMapAndroid.jar 참조 제거: Kakao Vector Map SDK와 충돌합니다.
    // implementation(files("libs/libDaumMapAndroid.jar")) // 이 라인은 반드시 제거해야 합니다!

    // 테스트 관련 의존성
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}