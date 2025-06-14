//app/src/main/java/com/example/parkinglot/dto/response/ParkingLotDetailDto.kt
package com.example.parkinglot.dto.response

import com.google.gson.annotations.SerializedName

data class ParkingLotDetail(
    // 주소 또는 고유 ID
    @SerializedName("addr")
    val id: String,

    // 빈자리 수
    @SerializedName("empty")
    val empty: Long,

    // 총 자리 수
    @SerializedName("total")
    val total: Long,

    // 혼잡도
    @SerializedName("ratio")
    val ratio: String, // PLENTY, MODERATE, BUSY, FULL

    // 요금
    @SerializedName("charge")
    val charge: String
)
