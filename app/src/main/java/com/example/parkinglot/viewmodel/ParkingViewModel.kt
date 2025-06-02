package com.example.parkinglot.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.dto.response.NearByParkinglotResponse
import com.example.parkinglot.mapper.toUiModelList
import com.example.parkinglot.repository.ParkingRepository
import com.example.parkinglot.uistate.ParkingLotUiState
import kotlinx.coroutines.launch

class ParkingViewModel : ViewModel() {

    var uiState by mutableStateOf(ParkingLotUiState())
        private set

    private val repository = ParkingRepository()

    private val _parkingData = MutableLiveData<NearByParkinglotResponse>()
    val parkingData: LiveData<NearByParkinglotResponse> = _parkingData

    // 주차장 정보 조회
    fun fetchParkingLots(request: NearByParkinglotRequest) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val result = repository.getNearbyParkingLots(request)
                uiState = uiState.copy(
                    parkingLots = result.parkingLot.toList(),
                    isLoading = false,
                    error = null
                )
                Log.d("ParkingViewModel", "API 성공: $result")
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "알 수 없는 오류 발생"
                )
                Log.e("ParkingViewModel", "API 실패: ${e.message}", e)
            }
        }
    }

    // 빈자리 필터
    fun fetchEmptyParkingLots(request: NearByParkinglotRequest) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val result = repository.getEmptyParkingLots(request)
                uiState = uiState.copy(
                    parkingLots = result.parkingLot.toList(),
                    isLoading = false,
                    error = null
                )
                Log.d("ParkingViewModel", "빈자리 필터 성공: $result")
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "빈자리 필터 오류 발생"
                )
                Log.e("ParkingViewModel", "빈자리 필터 실패: ${e.message}", e)
            }
        }
    }

    // 무료 필터
    fun fetchFreeParkingLots(request: NearByParkinglotRequest) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val result = repository.getFreeParkingLots(request)
                uiState = uiState.copy(
                    parkingLots = result.parkingLot.toList(),
                    isLoading = false,
                    error = null
                )
                Log.d("ParkingViewModel", "무료 필터 성공: $result")
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "무료 필터 오류 발생"
                )
                Log.e("ParkingViewModel", "무료 필터 실패: ${e.message}", e)
            }
        }
    }

    // 행정구 필터
    fun fetchRegionParkingLots(district: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val result = repository.getRegionParkingLots(district)
                uiState = uiState.copy(
                    parkingLots = result.parkingLot.toList(),
                    isLoading = false,
                    error = null
                )
                Log.d("ParkingViewModel", "행정구 필터 성공: $result")
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "행정구 필터 오류 발생"
                )
                Log.e("ParkingViewModel", "행정구 필터 실패: ${e.message}", e)
            }
        }
    }
}
