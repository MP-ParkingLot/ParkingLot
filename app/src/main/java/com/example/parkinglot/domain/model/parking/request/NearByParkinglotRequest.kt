//app/src/main/java/com/example/parkinglot/domain/model/parking/request/NearByParkingRequest.kt

package com.example.parkinglot.domain.model.parking.request

data class NearByParkinglotRequest(
    //사용자 정의 백엔드 서버 요청용
    val parkingLot: List<String>
)