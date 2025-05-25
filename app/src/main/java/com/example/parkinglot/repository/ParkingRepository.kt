package com.example.parkinglot.repository

import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.dto.response.EmptyParkinglotResponse
import com.example.parkinglot.dto.response.FreeParkinglotResponse
import com.example.parkinglot.dto.response.NearByParkinglotResponse
import com.example.parkinglot.dto.response.RegionParkinglotResponse

class ParkingRepository {
    //주차장 정보 조회
    suspend fun getNearbyParkingLots(request: NearByParkinglotRequest) =
        RetrofitClient.seoulParkingService.getNearbyParkingLots(request)

    //빈자리 필터
    suspend fun getEmptyParkingLots(request: NearByParkinglotRequest): EmptyParkinglotResponse {
        return RetrofitClient.seoulParkingService.getEmptyParkingLots(request)
    }

    //무료 필터
    suspend fun getFreeParkingLots(request: NearByParkinglotRequest): FreeParkinglotResponse {
        return RetrofitClient.seoulParkingService.getFreeParkingLots(request)
    }

    //행정 구역 별
    suspend fun getRegionParkingLots(district: String): RegionParkinglotResponse {
        return RetrofitClient.seoulParkingService.getRegionParkingLots(district)
    }
}
