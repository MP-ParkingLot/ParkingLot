//app/src/main/java/com/example/parkinglot/dto/network/kakao/KakaoAddressSearchService.kt

package com.example.parkinglot.data.network.kakao

import com.example.parkinglot.domain.model.kakao.KakaoLocalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// 카카오 주소 검색 API 인터페이스
interface KakaoAddressSearchService {
    @GET("/v2/local/search/address.json") // 주소 검색 API 엔드포인트
    suspend fun searchAddress(
        @Query("query") query: String, // 검색할 주소 또는 키워드
        @Header("Authorization") apiKey: String // 인증 키
    ): Response<KakaoLocalResponse> // AddressResponse DTO 반환
}