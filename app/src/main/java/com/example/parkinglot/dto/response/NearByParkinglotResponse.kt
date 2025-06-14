//app/src/main/java/com/example/parkinglot/dto/response/NearByParkinglotResponse.kt
package com.example.parkinglot.dto.response

// 1km 이내의 주차장 응답에 대한 데이터 구조를 나타내는 클래스
data class NearByParkinglotResponse(
    // 1km 이내의 주차장 상세 정보 목록
    val parkingLot: List<ParkingLotDetail>
)
