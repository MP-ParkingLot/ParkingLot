package com.example.parkinglot.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NearByParkinglotResponse(
    @SerialName("parkingLot")
    val parkingLot: Map<String, ParkingLotDetail>
)


