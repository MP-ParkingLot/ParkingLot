package com.example.parkinglot.service

import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.dto.response.NearByParkinglotResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SeoulParkingService {
    @POST("/parkinglot")
    suspend fun getNearbyParkingLots(
        @Body request: NearByParkinglotRequest
    ): NearByParkinglotResponse
}
