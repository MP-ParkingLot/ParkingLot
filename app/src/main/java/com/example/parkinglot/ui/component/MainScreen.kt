// app/src/main/java/com/example/parkinglot/ui/component/MainScreen.kt
package com.example.parkinglot.ui.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkinglot.uicomponent.KakaoMapScreen
import com.example.parkinglot.uistate.CombinedParkingLotInfo
import com.example.parkinglot.viewmodel.ParkingViewModel

@Composable
fun MainScreen(viewModel: ParkingViewModel = viewModel()) {
    // ViewModel 상태 구독
    val uiState by viewModel.uiState.collectAsState()
    val filteredParkingLots by viewModel.filteredParkingLots.collectAsState()
    var selectedParkingLot by remember { mutableStateOf<CombinedParkingLotInfo?>(null) }

    val currentLocation by viewModel.currentLocation.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val mapCenterRequest by viewModel.mapCenterMoveRequest.collectAsState()

    // 행정구 필터 적용 여부에 따라 VerticalFilterButtons의 활성화 상태 결정
    val areFilterButtonsEnabled = selectedDistrict.isEmpty()

    // 최초 위치 수신 시 데이터 로드
    // ★ 주의: 구 필터가 선택되지 않았을 때만 초기 로드 및 주변 데이터 로드를 수행합니다.
    LaunchedEffect(currentLocation, selectedDistrict) { // selectedDistrict를 dependency에 추가
        if (selectedDistrict.isEmpty()) { // 구 필터가 없는 경우에만 주변 데이터 로드
            currentLocation?.let { loc ->
                if (uiState.parkingLots.isEmpty() || selectedFilter == "거리") {
                    viewModel.fetchAllParkingLotData(loc.latitude, loc.longitude)
                    Log.d("MainScreen", "Initial data fetch triggered by location update or '거리' filter, and no district selected.")
                }
            } ?: run {
                Log.w("MainScreen", "Current location is null, cannot trigger initial data fetch when no district is selected.")
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        // 1) KakaoMapScreen
        KakaoMapScreen(
            modifier = Modifier.fillMaxSize(),
            parkingLots = filteredParkingLots,
            onMarkerClick = { lot -> selectedParkingLot = lot },
            onLocationUpdate = { loc -> viewModel.updateCurrentLocation(loc) },
            mapCenterRequest = mapCenterRequest,
            onMapCenterMoveHandled = viewModel::onMapCenterMoveHandled
        ){

        }

        // 2) 필터 버튼
        VerticalFilterButtons(
            selectedFilter = selectedFilter,
            onSelectFilter = { filter ->
                if (areFilterButtonsEnabled) { // 버튼이 활성화된 상태일 때만 필터 선택
                    viewModel.selectFilter(filter)
                }
            },
            isEnabled = areFilterButtonsEnabled, // 활성화 상태 전달
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 18.dp)
        )

        // 3) 구 선택 드롭다운
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 12.dp)
        ) {
            DistrictDropdownMenu(
                selectedDistrict = selectedDistrict,
                onDistrictSelected = { district ->
                    viewModel.selectDistrict(district) // 선택된 구로 데이터 로드 로직이 분기됨
                }
            )
        }

        // 4) 상세 다이얼로그 (생략 - 기존과 동일)
        selectedParkingLot?.let { lot ->
            val displayName = lot.LocationID?.takeIf { it.isNotBlank() }
                ?: lot.addressName?.takeIf { it.isNotBlank() }
                ?: "정보 없음"

            AlertDialog(
                onDismissRequest = { selectedParkingLot = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayName,
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (lot.ratio?.uppercase()) {
                                "PLENTY" -> "여유"
                                "MODERATE" -> "보통"
                                "BUSY" -> "혼잡"
                                "FULL" -> "만차"
                                else -> lot.ratio ?: "정보 없음"
                            },
                            fontSize = 14.sp,
                            color = when (lot.ratio?.uppercase()) {
                                "PLENTY" -> Color(0xFF4CAF50)
                                "MODERATE" -> Color(0xFFFFC107)
                                "BUSY", "FULL" -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = lot.LocationID
                                .takeUnless { it.isNullOrBlank() }
                                ?: lot.addressName
                                ?: "주소 정보 없음",
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(2.dp))
                        Text("요금", fontSize = 14.sp, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "시간당 ${lot.charge ?: "정보없음"}원",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "실시간 남은 자리: ${lot.empty}",
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedParkingLot = null }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        //navController.navigate("review_list/${lot.LocationID}")
                    }) {
                        Text("리뷰")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // 5) 로딩 & 에러 처리 (생략 - 기존과 동일)
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error?.let { errMsg ->
            Box(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("오류 발생: $errMsg", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        // 오류 발생 시 재시도 로직은 현재 선택된 구에 따라 달라져야 합니다.
                        if (selectedDistrict.isEmpty()) {
                            currentLocation?.let {
                                viewModel.fetchAllParkingLotData(it.latitude, it.longitude)
                            } ?: Log.w("MainScreen", "No current location, cannot retry initial fetch.")
                        } else {
                            // 현재 구 필터가 적용 중이면, 해당 구로 다시 검색
                            viewModel.selectDistrict(selectedDistrict)
                        }
                    }) {
                        Text("다시 시도")
                    }
                }
            }
        }
    }
}