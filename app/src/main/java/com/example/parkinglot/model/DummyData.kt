package com.example.parkinglot.model

import com.example.parkinglot.dto.request.NearByParkinglotRequest

object DummyData {
    val sampleRequest = NearByParkinglotRequest(
        parkingLot = listOf("장소ID_1", "장소ID_2", "장소ID_3", "장소ID_4")
    )
}