//app/src/main/java/com/example/parkinglot/repository/KakaoLocalRepository.kt
package com.example.parkinglot.repository

import com.example.parkinglot.BuildConfig
import com.example.parkinglot.dto.response.KakaoLocalResponse
import com.example.parkinglot.network.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 카카오 로컬 API와의 통신을 관리하는 레포지토리 클래스입니다.
// [KakaoLocalApiService]를 사용하여 네트워크 요청을 수행하고
// API 키 관리, Retrofit 및 OkHttpClient 설정을 담당합니다.
// 앱의 다른 컴포넌트(예: ViewModel)는 이 레포지토리를 통해 카카오 로컬 API 데이터를 요청합니다.
class KakaoLocalRepository {
    private val KAKAO_REST_API_KEY = BuildConfig.KAKAO_REST_KEY

    // 카카오 로컬 API 통신을 위한 Retrofit 서비스 인스턴스
    private val kakaoLocalApiService: KakaoLocalApiService


    init {
        // OkHttp 로깅 인터셉터 설정: 네트워크 요청 및 응답의 자세한 정보를 로그로 출력
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 요청 헤더, 바디, 응답 헤더, 바디 등 모든 정보 로깅
        }

        // OkHttpClient 생성: HTTP 요청을 수행하는 클라이언트
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging) // 로깅 인터셉터를 클라이언트에 추가
            .build()

        // Retrofit 인스턴스 생성: HTTP API를 Kotlin 인터페이스로 변환
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com") // 카카오 로컬 API 기본 URL
            .addConverterFactory(GsonConverterFactory.create()) // Gson 컨버터 사용
            .client(httpClient) // 로깅 인터셉터가 포함된 OkHttpClient 사용
            .build()

        // Retrofit 서비스 인터페이스 구현체 생성
        kakaoLocalApiService = retrofit.create(KakaoLocalApiService::class.java)
    }

    /**
     * 카테고리 기반으로 장소를 검색합니다 (주로 주변 주차장 검색용).
     * @param categoryGroupCode 검색할 카테고리 그룹 코드 (예: "PK6" for 주차장)
     * @param x 경도 (longitude)
     * @param y 위도 (latitude)
     * @param radius 반경 (미터), 기본값 2000m
     * @param query 추가 검색 키워드 (선택 사항)
     */
    suspend fun searchPlacesByCategory(
        categoryGroupCode: String,
        x: String, // 경도
        y: String,  // 위도
        radius: Int = 2000,
        query: String? = null
    ): KakaoLocalResponse? { // 반환 타입이 KakaoLocalResponse? 로 변경, null 가능성 포함
        val resp = kakaoLocalApiService.searchPlacesByCategory(
            apiKey = KAKAO_REST_API_KEY,
            categoryGroupCode = categoryGroupCode,
            x = x, // Double 타입으로 전달
            y = y,  // Double 타입으로 전달
            radius = radius,
            query = query
        )
        // 응답이 성공적이면 body()를 반환하고, 아니면 null 반환
        return if (resp.isSuccessful) resp.body() else null
    }

    /**
     * 주소 문자열을 이용하여 장소를 검색합니다 (지오코딩).
     * @param addressQuery 검색할 주소 문자열
     */
    suspend fun searchAddress(addressQuery: String): KakaoLocalResponse? { // 반환 타입이 KakaoLocalResponse? 로 변경, null 가능성 포함
        val resp = kakaoLocalApiService.searchAddress(
            apiKey = KAKAO_REST_API_KEY,
            query = addressQuery
        )
        // 응답이 성공적이면 body()를 반환하고, 아니면 null 반환
        return if (resp.isSuccessful) resp.body() else null
    }
}