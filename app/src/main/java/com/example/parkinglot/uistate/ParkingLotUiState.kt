package com.example.parkinglot.uistate

import com.example.parkinglot.dto.response.ParkingLotDetail

data class ParkingLotUiState(
    var showOnlyAvailable: Boolean = false, //빈자리 필터
    var showOnlyFree: Boolean = false, // 무료 필터
    val isLoading: Boolean = false,
    val error: String? = null,
    val parkingLots: List<Pair<String, ParkingLotDetail>> = emptyList()
)

