//app/src/main/java/com/example/parkinglot/dto/repository/kakao/KakaoLocalRepository.kt

package com.example.parkinglot.data.repository.kakao

import com.example.parkinglot.BuildConfig
import com.example.parkinglot.domain.model.kakao.KakaoLocalResponse
import com.example.parkinglot.data.network.kakao.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 카카오 **로컬 API** 래퍼
 *
 * * Retrofit/OkHttp 설정
 * * REST 키 관리
 * * 서비스 메서드 thin-wrapper
 *
 * “query(키워드)” 파라미터를 쓰지 않기로 했으므로, 관련 인자·전달 코드
 * 전부 **삭제**했습니다.
 */
class KakaoLocalRepository {

    /** 카카오 REST API 키 – `local.properties` → `BuildConfig` */
    private val KAKAO_REST_API_KEY = BuildConfig.KAKAO_REST_KEY

    /* Retrofit + OkHttp 로깅 설정 */
    private val kakaoLocalApiService: KakaoLocalApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        kakaoLocalApiService = retrofit.create(KakaoLocalApiService::class.java)
    }

    /* ─────────────────────────────────────────────
       1) 카테고리 기반 장소 검색 (PK6 – 주차장)
       ───────────────────────────────────────────── */
    suspend fun searchPlacesByCategory(
        categoryGroupCode: String = "PK6",
        longitude: Double,
        latitude:  Double,
        radius:    Int = 2000
    ): KakaoLocalResponse? {

        val resp = kakaoLocalApiService.searchPlacesByCategory(
            apiKey = "KakaoAK $KAKAO_REST_API_KEY",
            categoryGroupCode = categoryGroupCode,
            longitude = longitude,
            latitude  = latitude,
            radius    = radius
        )
        return if (resp.isSuccessful) resp.body() else null
    }

    /* ─────────────────────────────────────────────
       2) 주소 문자열 → 좌표(지오코딩)
       ───────────────────────────────────────────── */
    suspend fun searchAddress(addressQuery: String): KakaoLocalResponse? {
        val resp = kakaoLocalApiService.searchAddress(
            apiKey = "KakaoAK $KAKAO_REST_API_KEY",
            query  = addressQuery
        )
        return if (resp.isSuccessful) resp.body() else null
    }
}