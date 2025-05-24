package com.example.parkinglot.repository

import com.example.parkinglot.dto.request.NearByParkinglotRequest

class ParkingRepository {
    suspend fun getNearbyParkingLots(request: NearByParkinglotRequest) =
        RetrofitClient.seoulParkingService.getNearbyParkingLots(request)
}
