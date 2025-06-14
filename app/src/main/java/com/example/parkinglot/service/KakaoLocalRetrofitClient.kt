//app/src/main/java/com/example/parkinglot/service/KakaoLocalRetrofitClient.kt
package com.example.parkinglot.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoLocalRetrofitClient {
    private const val BASE_URL = "https://dapi.kakao.com/"

    private val retrofit: Retrofit by lazy {
        val gson = GsonBuilder()
            .setLenient() // JSON 파싱에 좀 더 유연하게 대응
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // 카테고리 검색 (주차장 등)을 위한 서비스
    val service: KakaoLocalApiService by lazy {
        retrofit.create(KakaoLocalApiService::class.java)
    }

    // ★추가: 주소 검색을 위한 서비스
    val addressSearchService: KakaoAddressSearchService by lazy {
        retrofit.create(KakaoAddressSearchService::class.java)
    }
}