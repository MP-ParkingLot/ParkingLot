//app/src/main/java/com/example/parkinglot/domain/model/parking/response/FreeParkinglotResponse.kt

package com.example.parkinglot.domain.model.parking.response

import com.example.parkinglot.domain.model.parking.ParkingLotDetail

// 요금이 무료인 주차장 응답에 대한 데이터 구조를 나타내는 클래스
data class FreeParkinglotResponse(

    // 요금이 무료인 주차장 상세 정보 목록
    val parkingLot: List<ParkingLotDetail>
)