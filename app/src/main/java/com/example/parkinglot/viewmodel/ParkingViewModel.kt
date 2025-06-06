package com.example.parkinglot.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkinglot.BuildConfig
import com.example.parkinglot.dto.response.ParkingLotDetail
import com.example.parkinglot.repository.ParkingRepository
import com.example.parkinglot.service.KakaoRetrofitClient
import com.example.parkinglot.uistate.ParkingLotUiState
import kotlinx.coroutines.launch

class ParkingViewModel : ViewModel() {

    var uiState by mutableStateOf(ParkingLotUiState())
        private set

    var filteredParkingLots by mutableStateOf<List<Pair<String, ParkingLotDetail>>>(emptyList())
        private set

    var selectedDistrict by mutableStateOf("강남구")
        private set

    private val repo = ParkingRepository()
    private val kakaoService = KakaoRetrofitClient.kakaoAddressSearchService

    private val TAG_PARSE = "ParkingViewModel-PARSE"
    private val TAG_GEO = "ParkingViewModel-GEO"

    fun fetchParkingLots(start: Int = 1, end: Int = 1000) = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true, error = null)
        try {
            val rows = repo.getParkingLotDetails(
                apiKey = BuildConfig.SEOUL_API_KEY,
                startIndex = start,
                endIndex = end
            )
            Log.d(TAG_PARSE, "rows=${rows.size}")

            val updated = rows.mapIndexed { index, lot ->
                val geoCodedLot = geocodeParkingLot(lot, index)
                "장소ID_$index" to geoCodedLot
            }

            uiState = uiState.copy(parkingLots = updated)
            filteredParkingLots = updated

        } catch (e: Exception) {
            uiState = uiState.copy(error = e.message)
            Log.e("ParkingViewModel", "주차장 데이터 로딩 실패", e)
        } finally {
            uiState = uiState.copy(isLoading = false)
        }
    }

    private suspend fun geocodeParkingLot(lot: ParkingLotDetail, index: Int): ParkingLotDetail {
        val address = lot.address ?: return lot
        return try {
            val response = kakaoService.searchAddress(
                query = address,
                apiKey = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"
            )

            if (response.isSuccessful) {
                response.body()?.documents?.firstOrNull()?.address?.let {
                    lot.lat = it.y.toDoubleOrNull()
                    lot.lng = it.x.toDoubleOrNull()
                    Log.d(TAG_GEO, "[$index] ✔ ${lot.name} ${lot.lat}, ${lot.lng}")
                }
            } else {
                Log.e(TAG_GEO, "[$index] ❌ ${lot.name} 응답 실패: ${response.code()}")
            }
            lot
        } catch (e: Exception) {
            Log.e(TAG_GEO, "[$index] ❌ ${lot.name} 예외: ${e.localizedMessage}")
            lot
        }
    }

    fun applyFilter(filterType: String) = viewModelScope.launch {
        val currentLots = uiState.parkingLots

        val newFilteredLots = when (filterType) {
            "빈 자리" -> currentLots.filter {
                val total = it.second.tpkct ?: 0
                val used = it.second.nowPrkVhclCnt ?: 0
                (total - used).coerceAtLeast(0) > 0
            }.sortedByDescending { (it.second.tpkct ?: 0) - (it.second.nowPrkVhclCnt ?: 0) }

            "무료" -> currentLots.filter {
                it.second.charge?.toIntOrNull() == 0
            }

            "행정구" -> {
                if (selectedDistrict.isNotEmpty() && selectedDistrict != "전체") {
                    currentLots.filter {
                        it.second.address?.contains(selectedDistrict) == true
                    }
                } else currentLots
            }

            "전체" -> currentLots
            else -> currentLots
        }

        filteredParkingLots = newFilteredLots
    }

    fun updateSelectedDistrict(district: String) {
        selectedDistrict = district
        applyFilter("행정구")
    }
}
