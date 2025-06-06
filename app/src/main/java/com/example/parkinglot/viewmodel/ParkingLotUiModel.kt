package com.example.parkinglot.model

data class ParkingLotUiModel(
    val empty: Int,
    val total: Int,
    val ratio: String,
    val charge: Int,
    val lat: Double,
    val lng: Double
)
