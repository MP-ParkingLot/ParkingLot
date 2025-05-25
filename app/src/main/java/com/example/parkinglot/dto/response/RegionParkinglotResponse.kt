package com.example.parkinglot.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionParkinglotResponse(
    @SerialName("parkingLot")
    val parkingLot: Map<String, ParkingLotDetail>
)

