//app/src/main/java/com/example/parkinglot/MyApplication.kt

package com.example.parkinglot

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk // Kakao Vector Map SDK 임포트

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kakao Vector Map SDK 초기화
        // BuildConfig.KAKAO_REST_KEY는 secrets-gradle-plugin이 secrets.properties에서 생성합니다.
        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY)
    }
}