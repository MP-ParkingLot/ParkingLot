// 위치: com.example.parkinglot.uistate
package com.example.parkinglot.uistate

data class ParkingLotUiModel(
    val id: String,
    val empty: String,
    val total: String,
    val ratio: String,
    val charge: Int,
    val isFree: Boolean
)
