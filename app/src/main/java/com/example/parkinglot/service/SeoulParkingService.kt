package com.example.parkinglot.service

import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.dto.response.EmptyParkinglotResponse
import com.example.parkinglot.dto.response.FreeParkinglotResponse
import com.example.parkinglot.dto.response.NearByParkinglotResponse
import com.example.parkinglot.dto.response.RegionParkinglotResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SeoulParkingService {
    @POST("/parkinglot") //주차장 정보 조회
    suspend fun getNearbyParkingLots(
        @Body request: NearByParkinglotRequest
    ): NearByParkinglotResponse

    @GET("/parkinglot") //행정 구역 별
    suspend fun getRegionParkingLots(
        @Query("district") district: String
    ): RegionParkinglotResponse

    @POST("/parkinglot/empty") //빈자리 필터
    suspend fun getEmptyParkingLots(
        @Body request: NearByParkinglotRequest
    ): EmptyParkinglotResponse

    @POST("/parkinglot/free") //무료 필터
    suspend fun getFreeParkingLots(
        @Body request: NearByParkinglotRequest
    ): FreeParkinglotResponse

}
