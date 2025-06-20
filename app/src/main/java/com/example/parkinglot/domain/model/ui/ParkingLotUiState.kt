//app/src/main/java/com/example/parkinglot/domain/model/ui/ParkingLotUiState.kt

package com.example.parkinglot.domain.model.ui

import com.example.parkinglot.domain.model.parking.CombinedParkingLotInfo

data class ParkingUiState(
    // 이 부분이 List<CombinedParkingLotInfo>로 변경됩니다.
    val parkingLots: List<CombinedParkingLotInfo> = emptyList(), // 전체 주차장 목록
    val filteredParkingLots: List<CombinedParkingLotInfo> = emptyList(), // 필터링된 주차장 목록 (새로 추가)
    val isLoading: Boolean = false,
    val error: String? = null
)