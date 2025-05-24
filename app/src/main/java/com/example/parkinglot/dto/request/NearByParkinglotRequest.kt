package com.example.parkinglot.dto.request

data class NearByParkinglotRequest(
    val parkingLot: List<String>
    //예:["장소ID_1", "장소ID_2", "장소ID_3", "장소ID_4"]
)
