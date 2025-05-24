package com.example.parkinglot.uistate

import com.example.parkinglot.dto.response.ParkingLotDetail

data class ParkingLotUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val parkingLots: List<Pair<String, ParkingLotDetail>> = emptyList()
)

