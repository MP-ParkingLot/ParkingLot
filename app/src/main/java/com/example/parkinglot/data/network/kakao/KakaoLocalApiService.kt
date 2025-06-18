//app/src/main/java/com/example/parkinglot/dto/network/kakao/KakaoLocalApiService.kt

package com.example.parkinglot.data.network.kakao

import com.example.parkinglot.domain.model.kakao.KakaoLocalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApiService {
    @GET("/v2/local/search/category.json")
    suspend fun searchPlacesByCategory(
        @Header("Authorization") apiKey: String,
        @Query("category_group_code") categoryGroupCode: String = "PK6", // 주차장 카테고리 코드
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Query("radius") radius: Int = 2000, // 미터 단위
        @Query("sort") sort: String = "distance" // 거리순 정렬
    ): Response<KakaoLocalResponse>

    @GET("/v2/local/geo/coord2address.json")
    suspend fun coord2address(
        @Header("Authorization") apiKey: String,

        @Query("x") longitude: Double,
        @Query("y") latitude:  Double
    ): Response<KakaoLocalResponse>

    @GET("/v2/local/search/address.json")
    suspend fun searchAddress(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String
    ): Response<KakaoLocalResponse>

}