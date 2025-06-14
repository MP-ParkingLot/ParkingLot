// app/src/main/java/com/example/parkinglot/viewmodel/ParkingViewModel.kt
package com.example.parkinglot.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkinglot.dto.response.ParkingLotDetail
import com.example.parkinglot.repository.ParkingLotRepository
import com.example.parkinglot.uistate.CombinedParkingLotInfo
import com.example.parkinglot.uistate.ParkingUiState
import com.example.parkinglot.util.Constants
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ParkingViewModel(
    private val parkingLotRepository: ParkingLotRepository
) : ViewModel() {

    /* ───────── 상태 플로우 ───────── */
    private val _uiState = MutableStateFlow(ParkingUiState())
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _selectedFilter = MutableStateFlow("")   // "" = 필터 없음
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedDistrict = MutableStateFlow("") // "" = 구 필터 없음
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

    private val _mapCenterMoveRequest = MutableStateFlow<LatLng?>(null)
    val mapCenterMoveRequest: StateFlow<LatLng?> = _mapCenterMoveRequest.asStateFlow()

    val filteredParkingLots: StateFlow<List<CombinedParkingLotInfo>> = _uiState
        .map { it.filteredParkingLots }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value.filteredParkingLots)

    fun updateCurrentLocation(location: Location) {
        _currentLocation.value = location
    }

    /* ─────────────────────────────────────────────
       ★ ADD : 현 위치 → 구 → 데이터 전부 가져오기
       ───────────────────────────────────────────── */
    fun refreshAroundMe(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // ① 좌표를 구 이름으로 변환
                val district = parkingLotRepository.coord2district(latitude, longitude)
                    ?: throw IllegalStateException("현재 위치의 구 정보를 가져올 수 없습니다.")

                // ② 그 구의 모든 주차장 상태 + 좌표 매핑
                val result = parkingLotRepository.getDistrictParkingLotsWithCoords(district)
                Log.d("ParkingViewModel", "aroundMe '$district' backend=${result.details.size}, coords=${result.coordMap.size}")

                val combined = buildCombinedList(result.details, result.coordMap)
                _uiState.update { state ->
                    state.copy(
                        parkingLots = combined,
                        isLoading   = false,
                        error       = null
                    )
                }

                // district 드롭다운에 표시 + 지도 중심 이동
                _selectedDistrict.value = district
                Constants.SEOUL_DISTRICT_CENTERS[district]
                    ?.let { _mapCenterMoveRequest.value = it }

                applyFilter()
            } catch (e: Exception) {
                Log.e("ParkingViewModel", "refreshAroundMe failed", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * 기존 A 방법 (반경 검색) – 거리·빈자리·무료 전용
     */
    fun fetchAllParkingLotData(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = parkingLotRepository.getNearByParkingLotsWithCoords(latitude, longitude)
                Log.d("ParkingViewModel", "fallback backend=${result.details.size}, coords=${result.coordMap.size}")

                val combined = buildCombinedList(result.details, result.coordMap)
                _uiState.update {
                    it.copy(
                        parkingLots = combined,
                        isLoading   = false,
                        error       = null
                    )
                }
                applyFilter()

            } catch (e: Exception) {
                Log.e("ParkingViewModel", "fetchAllParkingLotData failed", e)
                _uiState.update { it.copy(isLoading = false, error = "주변 데이터 로드 실패: ${e.message}") }
            }
        }
    }

    /* ───────────────────────────────────────────────
       ★ NEW: 상태+좌표 → CombinedParkingLotInfo 빌더
       ─────────────────────────────────────────────── */
    private fun buildCombinedList(
        details: List<ParkingLotDetail>,
        coordMap: Map<String, Pair<Double, Double>>
    ): List<CombinedParkingLotInfo> {
        val list = mutableListOf<CombinedParkingLotInfo>()
        details.forEach { lot ->
            val coord = coordMap[lot.id] ?: return@forEach
            val (lat, lon) = coord
            val safeEmpty = lot.empty.coerceAtLeast(0L)
            list.add(
                CombinedParkingLotInfo(
                    id               = lot.id,
                    LocationID       = lot.id,
                    addressName      = null,
                    roadAddressName  = null,
                    latitude         = lat,
                    longitude        = lon,
                    empty            = safeEmpty,
                    total            = lot.total,
                    ratio            = lot.ratio,
                    charge           = lot.charge,
                    region2depthName = ""
                )
            )
        }
        return list
    }

    /* ───────────────────────────────────────────────
       ※ 이하 로직(구 필터, applyFilter 등)은 동일
       ─────────────────────────────────────────────── */

    private fun fetchParkingLotsByDistrict(district: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = parkingLotRepository.getDistrictParkingLotsWithCoords(district)
                val combinedList = buildCombinedList(result.details, result.coordMap)
                _uiState.update {
                    it.copy(
                        parkingLots = combinedList,
                        isLoading   = false,
                        error       = null
                    )
                }
                applyFilter()
            } catch (e: Exception) {
                Log.e("ParkingViewModel", "fetchParkingLotsByDistrict failed for $district", e)
                _uiState.update { it.copy(isLoading = false, error = "구 데이터 로드 실패: ${e.message}") }
            }
        }
    }

    fun selectFilter(filter: String) {
        if (_selectedDistrict.value.isNotEmpty()
            && _selectedDistrict.value != "구 선택"
            && filter != "") {
            Log.d("ParkingViewModel", "Filter selection ignored: district filter is active.")
            return
        }
        _selectedFilter.value = filter
        applyFilter()
    }

    fun selectDistrict(district: String) {
        _selectedDistrict.value = district

        if (district == "구 선택" || district.isEmpty()) {
            _selectedFilter.value = ""
            _currentLocation.value?.let { loc ->
                refreshAroundMe(loc.latitude, loc.longitude)
            } ?: run {
                _uiState.update { it.copy(error = "현재 위치를 알 수 없어 주변 주차장을 로드할 수 없습니다.") }
            }
            _mapCenterMoveRequest.value = null
            _selectedDistrict.value = ""
        } else {
            _selectedFilter.value = ""
            Constants.SEOUL_DISTRICT_CENTERS[district]
                ?.let { _mapCenterMoveRequest.value = it }
            fetchParkingLotsByDistrict(district)
        }
    }

    private fun applyFilter() {
        val district = _selectedDistrict.value
        val filter   = _selectedFilter.value
        val baseList = _uiState.value.parkingLots

        val result = if (district.isNotEmpty() && district != "구 선택") {
            baseList
        } else {
            when (filter) {
                "거리" -> baseList.filter { it.hasValidCoords() }
                    .sortedBy { distToCurrentLocation(it) }
                "빈 자리" -> baseList.filter { it.empty > 0 && it.hasValidCoords() }
                    .sortedBy { distToCurrentLocation(it) }
                "무료" -> baseList.filter {
                    (it.charge == "0" || it.charge?.contains("무료") == true) && it.hasValidCoords()
                }.sortedBy { distToCurrentLocation(it) }
                else -> baseList
            }
        }

        Log.d("ParkingVM", "applyFilter ▶ district=$district, filter=$filter, 결과=${result.size}")
        _uiState.update { it.copy(filteredParkingLots = result) }
    }

    private fun CombinedParkingLotInfo.hasValidCoords() =
        this.latitude != 0.0 && this.longitude != 0.0

    private fun distToCurrentLocation(info: CombinedParkingLotInfo): Float {
        val loc = _currentLocation.value ?: return Float.MAX_VALUE
        return FloatArray(1).also {
            Location.distanceBetween(
                loc.latitude, loc.longitude,
                info.latitude, info.longitude,
                it
            )
        }[0]
    }

    fun onMapCenterMoveHandled() {
        _mapCenterMoveRequest.value = null
        Log.d("ParkingViewModel", "Map center move request handled.")
    }
}
