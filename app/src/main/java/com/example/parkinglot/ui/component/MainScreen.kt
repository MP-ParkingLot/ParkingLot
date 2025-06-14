// app/src/main/java/com/example/parkinglot/ui/component/MainScreen.kt
package com.example.parkinglot.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun MainScreen(
    viewModel: ParkingViewModel = viewModel(),
    onNavigateToReview: (String) -> Unit = {}      // ⭐ 기본값 제공
) {
    /* ───── 1) 상태 구독 ───── */
    val uiState             by viewModel.uiState.collectAsState()
    val filteredParkingLots by viewModel.filteredParkingLots.collectAsState()
    var selectedParkingLot  by remember { mutableStateOf<CombinedParkingLotInfo?>(null) }

    val currentLocation  by viewModel.currentLocation.collectAsState()
    val selectedFilter   by viewModel.selectedFilter.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val mapCenterRequest by viewModel.mapCenterMoveRequest.collectAsState()

    val areFilterButtonsEnabled = selectedDistrict.isEmpty()

    /* ─────────────────────────────────────
       2) 최초 위치 수신 → 데이터 로드
       ───────────────────────────────────── */
    LaunchedEffect(currentLocation, selectedDistrict) {
        if (selectedDistrict.isEmpty()) {
            currentLocation?.let { loc ->
                if (uiState.parkingLots.isEmpty() || selectedFilter == "거리") {
                    viewModel.refreshAroundMe(loc.latitude, loc.longitude)
                    Log.d("MainScreen", "🔄 Data fetched (no district filter)")
                }
            } ?: Log.w("MainScreen", "Current location null")
        }
    }

    /* ─────────────────────────────────────
       3) UI 레이아웃
       ───────────────────────────────────── */
    Box(Modifier.fillMaxSize()) {

        /* 3-1  지도 */
        KakaoMapScreen(
            modifier               = Modifier.fillMaxSize(),
            parkingLots            = filteredParkingLots,
            onMarkerClick          = { lot -> selectedParkingLot = lot },
            onLocationUpdate       = { viewModel.updateCurrentLocation(it) },
            mapCenterRequest       = mapCenterRequest,
            onMapCenterMoveHandled = viewModel::onMapCenterMoveHandled   // ← 끝 콤마 삭제
        )

        /* 3-2  세로 필터 버튼 */
        VerticalFilterButtons(
            selectedFilter = selectedFilter,
            onSelectFilter = { if (areFilterButtonsEnabled) viewModel.selectFilter(it) },
            isEnabled      = areFilterButtonsEnabled,
            modifier       = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 18.dp)
        )

        /* 3-3  구(區) 드롭다운 */
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 12.dp)
        ) {
            DistrictDropdownMenu(
                selectedDistrict = selectedDistrict,
                onDistrictSelected = viewModel::selectDistrict
            )
        }

        /* 3-4  상세 다이얼로그 */
        selectedParkingLot?.let { lot ->
            val displayName = lot.LocationID.takeUnless { it.isNullOrBlank() }
                ?: lot.addressName.takeUnless { it.isNullOrBlank() }
                ?: "정보 없음"

            AlertDialog(
                onDismissRequest = { selectedParkingLot = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text  = displayName,
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text  = when (lot.ratio?.uppercase()) {
                                "PLENTY"   -> "여유"
                                "MODERATE" -> "보통"
                                "BUSY"     -> "혼잡"
                                "FULL"     -> "만차"
                                else       -> lot.ratio ?: "정보 없음"
                            },
                            fontSize = 14.sp,
                            color    = when (lot.ratio?.uppercase()) {
                                "PLENTY"   -> Color(0xFF4CAF50)
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
                            text  = lot.LocationID.takeUnless { it.isNullOrBlank() }
                                ?: lot.addressName ?: "주소 정보 없음",
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text("요금", fontSize = 14.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "시간당 ${lot.charge ?: "정보없음"}원",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "실시간 남은 자리: ${lot.empty}",
                            fontSize = 14.sp
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
                        // ① LocationID → ② addressName → ③ 취소
                        val idForReview = lot.LocationID
                            ?.takeIf { it.isNotBlank() }
                            ?: lot.addressName                 // 주소라도 넘긴다
                            ?: return@OutlinedButton           // 둘 다 없으면 아무 일도 안 함

                        onNavigateToReview(idForReview)        // ★ 반드시 호출
                        selectedParkingLot = null
                    }) {
                        Text("리뷰")
                    }
                },
                shape           = RoundedCornerShape(12.dp),
                containerColor  = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        /* 3-5  로딩 & 에러 UI */
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error?.let { msg ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("오류 발생: $msg", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (selectedDistrict.isEmpty()) {
                            currentLocation?.let {
                                viewModel.refreshAroundMe(it.latitude, it.longitude)
                            }
                        } else {
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
