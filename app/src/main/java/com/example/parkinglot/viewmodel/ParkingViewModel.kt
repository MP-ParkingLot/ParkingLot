// app/src/main/java/com/example/parkinglot/viewmodel/ParkingViewModel.kt
package com.example.parkinglot.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _uiState = MutableStateFlow(ParkingUiState())
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _selectedFilter = MutableStateFlow("")   // "" = 필터 없음
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // _selectedDistrict는 이제 "구 선택" 문자열도 가질 수 있음
    private val _selectedDistrict = MutableStateFlow("")  // "" = 구 필터 없음 (초기 상태)
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

    private val _mapCenterMoveRequest = MutableStateFlow<LatLng?>(null)
    val mapCenterMoveRequest: StateFlow<LatLng?> = _mapCenterMoveRequest.asStateFlow()

    val filteredParkingLots: StateFlow<List<CombinedParkingLotInfo>> = _uiState
        .map { it.filteredParkingLots }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value.filteredParkingLots)

    fun updateCurrentLocation(location: Location) {
        _currentLocation.value = location
    }

    /**
     * 현재 위치 기반으로 주변 주차장 데이터를 가져옵니다.
     * 이 함수는 주로 초기 로드 또는 구 필터가 해제되었을 때 사용됩니다.
     */
    fun fetchAllParkingLotData(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // 1. 백엔드에서 주차장 상세 정보를 가져옵니다. (주변 기준)
                val parkingLotDetails = parkingLotRepository.getNearByParkingLots(latitude, longitude)
                Log.d("ParkingViewModel", "1. Fetched parkingLotDetails size from backend (nearby): ${parkingLotDetails.size}")
                if (parkingLotDetails.isEmpty()) {
                    Log.w("ParkingViewModel", "1. No parking lot details fetched from backend/repository (nearby).")
                }

                val combinedList = mutableListOf<CombinedParkingLotInfo>()
                for (lot in parkingLotDetails) {
                    val safeEmpty = (lot.empty).coerceAtLeast(0L)
                    val searchString = lot.id // 백엔드 ID가 검색 가능한 주차장 이름/주소라고 가정

                    val addressDocs = parkingLotRepository.searchParkingLotsByAddress(searchString)
                    val bestMatch = addressDocs.firstOrNull()

                    if (bestMatch == null) {
                        Log.w("ParkingViewModel", "2. 카카오 로컬 API에서 '${searchString}'에 대한 검색 결과가 없습니다. 이 주차장은 지도에 표시되지 않습니다.")
                        continue // 다음 주차장으로 넘어감
                    }

                    val finalLat = bestMatch.y?.toDoubleOrNull() ?: 0.0
                    val finalLon = bestMatch.x?.toDoubleOrNull() ?: 0.0

                    if (finalLat == 0.0 || finalLon == 0.0) {
                        Log.w("ParkingViewModel", "3. 카카오 로컬 API 검색 결과 '${bestMatch.placeName}'의 좌표가 0.0 입니다. lat=$finalLat, lon=$finalLon. 이 주차장은 지도에 표시되지 않습니다.")
                        continue // 유효하지 않은 좌표도 제외
                    }

                    val districtFromApi = bestMatch.addressDetail?.region2depthName
                        ?: bestMatch.roadAddressDetail?.region2depthName

                    Log.d("ParkingViewModel", "4. Combined Info for ID '${lot.id}' (search: '$searchString'): lat=$finalLat, lon=$finalLon, placeName=${bestMatch.placeName}, district=$districtFromApi")

                    combinedList.add(
                        CombinedParkingLotInfo(
                            id = lot.id,
                            LocationID = bestMatch.placeName,
                            addressName = bestMatch.addressName,
                            roadAddressName = bestMatch.roadAddressName,
                            latitude = finalLat,
                            longitude = finalLon,
                            empty = safeEmpty,
                            total = lot.total,
                            ratio = lot.ratio,
                            charge = lot.charge,
                            region2depthName = districtFromApi ?: ""
                        )
                    )
                }

                Log.d("ParkingViewModel", "5. Final combinedList size (after filtering invalid coords, nearby): ${combinedList.size}")

                _uiState.update {
                    it.copy(
                        parkingLots = combinedList, // 전체 주차장 목록 업데이트
                        isLoading = false,
                        error = null
                    )
                }
                applyFilter() // 데이터 로드 후 필터 적용 (현재는 거리 기준이 default)
            } catch (e: Exception) {
                Log.e("ParkingViewModel", "fetchAllParkingLotData failed", e)
                _uiState.update { it.copy(isLoading = false, error = "주변 데이터 로드 실패: ${e.message}") }
            }
        }
    }


    /**
     * 특정 구에 해당하는 주차장 데이터를 가져옵니다.
     * 이 함수는 구 필터가 선택되었을 때만 사용됩니다.
     */
    private fun fetchParkingLotsByDistrict(district: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // 1. 백엔드에서 특정 구의 주차장 상세 정보를 가져옵니다.
                val parkingLotDetails = parkingLotRepository.getParkingLotsByDistrict(district)
                Log.d("ParkingViewModel", "1. Fetched parkingLotDetails size from backend (district '$district'): ${parkingLotDetails.size}")
                if (parkingLotDetails.isEmpty()) {
                    Log.w("ParkingViewModel", "1. No parking lot details fetched from backend/repository for district '$district'.")
                }

                val combinedList = mutableListOf<CombinedParkingLotInfo>()
                for (lot in parkingLotDetails) {
                    val safeEmpty = (lot.empty).coerceAtLeast(0L)
                    val searchString = lot.id // 백엔드 ID가 검색 가능한 주차장 이름/주소라고 가정

                    val addressDocs = parkingLotRepository.searchParkingLotsByAddress(searchString)
                    val bestMatch = addressDocs.firstOrNull()

                    if (bestMatch == null) {
                        Log.w("ParkingViewModel", "2. 카카오 로컬 API에서 '${searchString}'에 대한 검색 결과가 없습니다. 이 주차장은 지도에 표시되지 않습니다.")
                        continue
                    }

                    val finalLat = bestMatch.y?.toDoubleOrNull() ?: 0.0
                    val finalLon = bestMatch.x?.toDoubleOrNull() ?: 0.0

                    if (finalLat == 0.0 || finalLon == 0.0) {
                        Log.w("ParkingViewModel", "3. 카카오 로컬 API 검색 결과 '${bestMatch.placeName}'의 좌표가 0.0 입니다. lat=$finalLat, lon=$finalLon. 이 주차장은 지도에 표시되지 않습니다.")
                        continue
                    }

                    val districtFromApi = bestMatch.addressDetail?.region2depthName
                        ?: bestMatch.roadAddressDetail?.region2depthName

                    Log.d("ParkingViewModel", "4. Combined Info for ID '${lot.id}' (search: '$searchString'): lat=$finalLat, lon=$finalLon, placeName=${bestMatch.placeName}, district=$districtFromApi")

                    combinedList.add(
                        CombinedParkingLotInfo(
                            id = lot.id,
                            LocationID = bestMatch.placeName,
                            addressName = bestMatch.addressName,
                            roadAddressName = bestMatch.roadAddressName,
                            latitude = finalLat,
                            longitude = finalLon,
                            empty = safeEmpty,
                            total = lot.total,
                            ratio = lot.ratio,
                            charge = lot.charge,
                            region2depthName = districtFromApi ?: ""
                        )
                    )
                }

                Log.d("ParkingViewModel", "5. Final combinedList size (after filtering invalid coords, district '$district'): ${combinedList.size}")

                _uiState.update {
                    it.copy(
                        parkingLots = combinedList, // 특정 구의 주차장 목록으로 업데이트
                        isLoading = false,
                        error = null
                    )
                }
                applyFilter() // 데이터 로드 후 필터 적용 (이 경우 구 필터가 적용된 상태)
            } catch (e: Exception) {
                Log.e("ParkingViewModel", "fetchParkingLotsByDistrict failed for $district", e)
                _uiState.update { it.copy(isLoading = false, error = "구 데이터 로드 실패: ${e.message}") }
            }
        }
    }


    fun selectFilter(filter: String) {
        // 구 필터가 "구 선택"이 아닌 다른 구로 적용 중이면, 주변 필터 선택을 무시
        if (_selectedDistrict.value.isNotEmpty() && _selectedDistrict.value != "구 선택" && filter != "") {
            Log.d("ParkingViewModel", "Filter selection ignored: district filter is active.")
            return
        }
        _selectedFilter.value = filter
        applyFilter()
    }

    /**
     * 구 선택 시 필터 적용 + 지도 중심 이동 요청
     * 선택된 구에 따라 데이터 로드 방식이 달라집니다.
     */
    fun selectDistrict(district: String) {
        // 1. 구 상태 업데이트
        _selectedDistrict.value = district

        // 2. 구 필터가 해제될 때 ("구 선택" 문자열이 선택되었을 때)
        if (district == "구 선택" || district.isEmpty()) { // isEmpty도 함께 처리 (초기 상태 포함)
            _selectedFilter.value = "" // 다른 필터들을 "전체"로 초기화 (활성화)
            _currentLocation.value?.let { loc ->
                // 현재 위치 기반으로 주변 주차장 데이터를 다시 가져옴
                fetchAllParkingLotData(loc.latitude, loc.longitude)
                Log.d("ParkingViewModel", "구 필터 해제: 주변 주차장 데이터 재로드 요청")
            } ?: run {
                _uiState.update { it.copy(error = "현재 위치를 알 수 없어 주변 주차장을 로드할 수 없습니다.") }
                Log.e("ParkingViewModel", "Cannot fetch nearby parking lots: currentLocation is null on district filter clear.")
            }
            _mapCenterMoveRequest.value = null // 지도 중심 이동 요청도 초기화 (필요하다면)
            _selectedDistrict.value = "" // 실제 내부 상태는 빈 문자열로 초기화
        } else {
            // 특정 구가 선택된 경우:
            _selectedFilter.value = "" // 구 필터 선택 시 주변 필터는 "전체"로 초기화 (비활성화 상태 유지)

            // 지도 중심 이동 요청
            Constants.SEOUL_DISTRICT_CENTERS[district]?.let { latLng ->
                _mapCenterMoveRequest.value = latLng
                Log.d("ParkingViewModel", "Map center move request for district '$district': $latLng")
            } ?: Log.w("ParkingViewModel", "No center coordinates for district '$district'")

            // 해당 구의 모든 주차장 데이터를 백엔드에서 가져옴
            fetchParkingLotsByDistrict(district)
        }
    }

    private fun applyFilter() {
        val district = _selectedDistrict.value
        val filter   = _selectedFilter.value

        val baseList = _uiState.value.parkingLots // 이미 적절한 데이터셋이 여기에 들어있음

        // "구 선택"이거나 빈 문자열일 때만 주변 필터 적용
        val result = if (district.isNotEmpty() && district != "구 선택") {
            // 특정 구 필터가 적용 중일 때는 다른 필터를 적용하지 않고
            // _uiState.value.parkingLots (이미 구로 필터링된 데이터)를 그대로 사용
            baseList
        } else {
            // 구 필터가 적용되지 않았거나 "구 선택"일 때만 주변 필터 적용
            when (filter) {
                "거리" -> baseList
                    .filter { it.hasValidCoords() }
                    .sortedBy { distToCurrentLocation(it) }
                "빈 자리" -> baseList
                    .filter { it.empty > 0 && it.hasValidCoords() }
                    .sortedByDescending { it.empty }
                "무료" -> baseList
                    .filter { (it.charge == "0" || it.charge?.contains("무료") == true) && it.hasValidCoords() }
                else -> baseList // "전체" 필터 (기본값)
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