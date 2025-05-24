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
import com.example.parkinglot.repository.ParkingRepository
import com.example.parkinglot.uistate.ParkingLotUiState
import kotlinx.coroutines.launch

class ParkingViewModel : ViewModel() {

    var uiState by mutableStateOf(ParkingLotUiState())
    private set
    private val repository = ParkingRepository()

    private val _parkingData = MutableLiveData<NearByParkinglotResponse>()
    val parkingData: LiveData<NearByParkinglotResponse> = _parkingData

    fun fetchParkingLots(request: NearByParkinglotRequest) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val result = repository.getNearbyParkingLots(request)
                uiState = uiState.copy(
                    parkingLots = result.parkingLot.toList(), // map → list 변환 필요 시
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



}


