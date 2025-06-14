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

    /* ── 기존 상태들 동일 ── */
    private val _uiState = MutableStateFlow(ParkingUiState())
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _selectedFilter = MutableStateFlow("")        // "", "거리", …
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    /** ⬇ 〈Int〉 → 〈Float〉 로 변경 */
    private val _distanceKm = MutableStateFlow(1.0f)          // 기본 1 km
    val distanceKm: StateFlow<Float> = _distanceKm.asStateFlow()

    private val _selectedDistrict = MutableStateFlow("")
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

    private val _mapCenterMoveRequest = MutableStateFlow<LatLng?>(null)
    val mapCenterMoveRequest: StateFlow<LatLng?> = _mapCenterMoveRequest.asStateFlow()

    val filteredParkingLots: StateFlow<List<CombinedParkingLotInfo>> = _uiState
        .map { it.filteredParkingLots }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /* ────────────────────────────── */
    /* 현재 위치 갱신                  */
    /* ────────────────────────────── */

    /** 거리 반경 변경 – 0.5 ~ 10 km 사이 */
    fun setDistanceKm(km: Float) {
        _distanceKm.value = km.coerceIn(0.5f, 10f)
        if (_selectedFilter.value == "거리") applyFilter()
    }

    /* ───────────────────────────────────────────── */

    fun updateCurrentLocation(loc: Location) {
        val prev = _currentLocation.value
        _currentLocation.value = loc

        //  100 m 이상 이동 시에만 재조회
        if (prev == null || loc.distanceTo(prev) > 100f) {
            /* ★ 현재 구 필터가 비어 있으면 autoSetDistrict = false */
            val keepDistrict = _selectedDistrict.value.isNotEmpty()
            refreshAroundMe(
                lat             = loc.latitude,
                lon             = loc.longitude,
                autoSetDistrict = keepDistrict      // ← 핵심
            )
        }
    }

    /* ─────────────────────────────────────────────
       1) 현 위치 → 구 → 전체 주차장 세트 로드
       ───────────────────────────────────────────── */
    /**  autoSetDistrict = true 이면 _selectedDistrict 를 채운다 */
    fun refreshAroundMe(
        lat: Double,
        lon: Double,
        autoSetDistrict: Boolean = true
    ) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val district = parkingLotRepository.getDistrictByCoords(lat, lon)
                    ?: throw IllegalStateException("구 정보를 찾지 못했습니다")

                val result = parkingLotRepository.getDistrictParkingLotsWithCoords(district)

                /* ── 핵심: 구 필터 상태 결정 ── */
                if (autoSetDistrict) {
                    _selectedDistrict.value = district
                    _mapCenterMoveRequest.value =
                        Constants.SEOUL_DISTRICT_CENTERS[district]
                } else {
                    _selectedDistrict.value = ""      // 선택 해제 유지
                    _mapCenterMoveRequest.value = null
                }

                val combined = buildCombinedList(result.details, result.coordMap)
                _uiState.update { it.copy(parkingLots = combined, isLoading = false) }

                /* ✅ “거리” 필터는 autoSetDistrict 가 true 일 때만 자동 지정 */
                if (autoSetDistrict) {
                    _selectedFilter.value = "거리"
                }
                applyFilter()

            } catch (e: Exception) {
                Log.e("ParkingVM", "refreshAroundMe error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    /* ─────────────────────────────────────────────
       2) 구 드롭다운으로 선택된 경우
       ───────────────────────────────────────────── */
    fun selectDistrict(district: String) {

        /* ①  “구 선택”  또는  빈 문자열  →  구 필터 해제 */
        if (district.isBlank() || district == "구 선택") {
            _selectedDistrict.value = ""      // ⭐ 구 필터 해제
            _selectedFilter.value = ""      // 거리/빈자리/무료 초기화
            _mapCenterMoveRequest.value = null

            // 현 위치 기준으로 재조회
            _currentLocation.value?.let { loc ->
                refreshAroundMe(
                    lat = loc.latitude,
                    lon = loc.longitude,
                    autoSetDistrict = false
                )
            } ?: Log.w("ParkingVM", "Current location null - cannot refresh")

            return
        }

        /* ② 실제 구가 선택된 경우 */
        _selectedDistrict.value = district
        _selectedFilter.value = ""          // 거리·빈자리·무료 초기화
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = parkingLotRepository.getDistrictParkingLotsWithCoords(district)
                Log.d(
                    "ParkingVM",
                    "selectDistrict: backend=${result.details.size} coords=${result.coordMap.size}"
                )

                val combined = buildCombinedList(result.details, result.coordMap)
                _uiState.update { it.copy(parkingLots = combined, isLoading = false) }

                _mapCenterMoveRequest.value = Constants.SEOUL_DISTRICT_CENTERS[district]
                applyFilter()

            } catch (e: Exception) {
                Log.e("ParkingVM", "selectDistrict error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /* ─────────────────────────────────────────────
       3) 필터 버튼 처리
       ───────────────────────────────────────────── */
    fun selectFilter(filter: String) {
        _selectedFilter.value = filter

        when (filter) {
            "빈 자리" -> fetchEmptyOrFree(isEmpty = true)
            "무료" -> fetchEmptyOrFree(isEmpty = false)
            // 거리·전체는 local filter
        }
        applyFilter()
    }

    /* ─────────────────────────────────────────────
       4) “빈자리/무료” POST 재조회
       ───────────────────────────────────────────── */
    private fun fetchEmptyOrFree(isEmpty: Boolean) {
        val loc = _currentLocation.value ?: return
        viewModelScope.launch {
            val list: List<ParkingLotDetail> = try {
                if (isEmpty) {
                    parkingLotRepository.getEmptyParkingLots(loc.latitude, loc.longitude)
                } else {
                    parkingLotRepository.getFreeParkingLots(loc.latitude, loc.longitude)
                }
            } catch (e: Exception) {
                Log.e("ParkingVM", "empty/free fetch error", e)
                _uiState.update { it.copy(error = e.message) }
                return@launch
            }

            // 기존 좌표 매핑 유지
            val coordMap = _uiState.value.parkingLots.associateBy { it.id }
                .mapValues { it.value.latitude to it.value.longitude }

            val combined = buildCombinedList(list, coordMap)
            _uiState.update { it.copy(parkingLots = combined) }
            applyFilter()   // 필터 다시
        }
    }

    /* ─────────────────────────────────────────────
       5) 필터 적용 로직
       ───────────────────────────────────────────── */
    private fun applyFilter() {
        val filter   = _selectedFilter.value
        val baseList = _uiState.value.parkingLots
        val loc      = _currentLocation.value

        val result = when (filter) {
            "거리" -> {
                val radiusM = (_distanceKm.value * 1000).toInt()
                baseList.filter { info ->
                    loc != null && info.hasValidCoords() &&
                            info.distanceTo(loc) <= radiusM
                }.sortedBy { info -> loc?.let { info.distanceTo(it) } ?: Float.MAX_VALUE }
            }
            "빈 자리" -> baseList.filter { it.empty > 0 }
            "무료"   -> baseList.filter { it.charge == "0" || it.charge?.contains("무료") == true }
            else      -> baseList
        }

        Log.d("ParkingVM", "applyFilter ▶ filter=$filter, km=${_distanceKm.value}, 결과=${result.size}")
        _uiState.update { it.copy(filteredParkingLots = result) }
    }

    /* ─────────────────────────────────────────────
       6) 헬퍼
       ───────────────────────────────────────────── */
    private fun CombinedParkingLotInfo.hasValidCoords() =
        latitude != 0.0 && longitude != 0.0

    private fun CombinedParkingLotInfo.distanceTo(loc: Location): Float =
        FloatArray(1).also {
            Location.distanceBetween(
                loc.latitude, loc.longitude,
                latitude, longitude,
                it
            )
        }[0]

    private fun buildCombinedList(
        details: List<ParkingLotDetail>,
        coordMap: Map<String, Pair<Double, Double>>
    ): List<CombinedParkingLotInfo> =
        details.mapNotNull { lot ->
            val (lat, lon) = coordMap[lot.id] ?: return@mapNotNull null
            CombinedParkingLotInfo(
                id = lot.id,
                LocationID = lot.id,
                addressName = null,
                roadAddressName = null,
                latitude = lat,
                longitude = lon,
                empty = lot.empty.coerceAtLeast(0),
                total = lot.total,
                ratio = lot.ratio,
                charge = lot.charge,
                region2depthName = ""
            )
        }

    fun onMapCenterMoveHandled() {
        _mapCenterMoveRequest.value = null
    }
}
