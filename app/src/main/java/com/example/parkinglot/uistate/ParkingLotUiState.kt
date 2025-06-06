// com/example/parkinglot/uistate/ParkingLotUiState.kt
package com.example.parkinglot.uistate

import com.example.parkinglot.dto.response.ParkingLotDetail

data class ParkingLotUiState(
    val parkingLots: List<Pair<String, ParkingLotDetail>> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
