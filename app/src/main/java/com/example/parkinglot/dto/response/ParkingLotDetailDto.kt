package com.example.parkinglot.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotDetail(
    @SerialName("empty")
    val empty: String,

    @SerialName("total")
    val total: String,

    @SerialName("ratio")
    val ratio: String,

    @SerialName("charge")
    val charge: Int
)

