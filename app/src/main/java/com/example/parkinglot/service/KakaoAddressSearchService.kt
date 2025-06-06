// com/example/parkinglot/service/KakaoAddressSearchService.kt
package com.example.parkinglot.service

import com.example.parkinglot.dto.response.KakaoAddressResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAddressSearchService {
    @GET("v2/local/search/address.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        // @Header("Authorization") 어노테이션을 사용하여 "KakaoAK " 접두사를 포함한 키를 전달합니다.
        @Header("Authorization") apiKey: String
    ): Response<KakaoAddressResponse>
}