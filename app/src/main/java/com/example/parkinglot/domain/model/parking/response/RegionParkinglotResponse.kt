//app/src/main/java/com/example/parkinglot/domain/model/parking/response/RegionParkinglotResponse.kt

package com.example.parkinglot.domain.model.parking.response

import com.example.parkinglot.domain.model.parking.ParkingLotDetail

// 행정구별 주차장 응답에 대한 데이터 구조를 나타내는 클래스
data class RegionParkinglotResponse(
    // 행정구 별 주차장 상세 정보 목록
    val parkingLot: List<ParkingLotDetail>
)
