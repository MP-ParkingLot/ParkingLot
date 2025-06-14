//app/src/main/java/com/example/parkinglot/network/KakaoLocalApiService.kt
package com.example.parkinglot.network

import com.example.parkinglot.dto.response.KakaoLocalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


// 카카오 로컬 API와 통신하기 위한 Retrofit 인터페이스입니다.
// 장소 검색 (예: 카테고리별 주차장 검색) 및 주소 검색 기능을 제공합니다.
interface KakaoLocalApiService {

    // 특정 카테고리 그룹 코드와 위치(좌표, 반경)를 기반으로 장소 정보를 검색합니다.
    // 주로 주변 주차장 검색에 사용될 수 있습니다.
    @GET("/v2/local/search/category.json")
    suspend fun searchPlacesByCategory(
        @Header("Authorization") apiKey: String, // "KakaoAK YOUR_REST_API_KEY"
        @Query("category_group_code") categoryGroupCode: String, // 예: PK6 (주차장)
        @Query("x") x: String, // 경도 (쿼리 파라미터 이름과 Kotlin 파라미터 이름 일치)
        @Query("y") y: String,  // 위도 (쿼리 파라미터 이름과 Kotlin 파라미터 이름 일치)
        @Query("radius") radius: Int = 2000, // 반경 (m)
        @Query("query") query: String? = null // 검색 쿼리 (선택 사항)
    ): Response<KakaoLocalResponse>

    // 특정 주소 문자열을 기반으로 주소 정보를 검색합니다.
    // 예를 들어, 주차장의 상세 주소를 검색하여 정확한 위도/경도를 얻을 때 사용될 수 있습니다.
    @GET("/v2/local/search/address.json")
    suspend fun searchAddress(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String
    ): Response<KakaoLocalResponse>
}