// com/example/parkinglot/service/KakaoRetrofitClient.kt
package com.example.parkinglot.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoRetrofitClient {
    private const val BASE_URL = "https://dapi.kakao.com/"

    // lazy 초기화를 사용하여 불필요한 객체 생성을 방지합니다.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val kakaoAddressSearchService: KakaoAddressSearchService by lazy {
        retrofit.create(KakaoAddressSearchService::class.java)
    }
}