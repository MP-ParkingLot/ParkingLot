// com/example/parkinglot/uicomponent/KakaoMapScreen.kt
package com.example.parkinglot.uicomponent

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.parkinglot.map.MapMarkerManager
import com.example.parkinglot.viewmodel.ParkingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun KakaoMapScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ✅ ViewModel 수동 생성 (테스트용)
    val viewModel = remember { ParkingViewModel() }

    val parkingLots = viewModel.filteredParkingLots
    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val mapView = remember { MapView(context) }

    // ✅ 강제 호출로 테스트
    LaunchedEffect(Unit) {
        Log.d("DEBUG_INIT", "💥 fetchParkingLots() 강제 호출")
        viewModel.fetchParkingLots()
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.start(
                object : MapLifeCycleCallback() {
                    override fun onMapResumed() {}
                    override fun onMapDestroy() {}
                    override fun onMapError(p0: Exception?) {
                        Log.e("KAKAO_MAP", "지도 오류 발생", p0)
                    }
                },
                object : KakaoMapReadyCallback() {
                    override fun onMapReady(map: KakaoMap) {
                        Log.d("KAKAO_MAP", "지도 준비 완료")
                        kakaoMapState.value = map

                        val validLots = parkingLots.map { it.second }
                            .filter { it.lat != null && it.lng != null }

                        Log.d("KAKAO_MAP", "초기 유효 주차장 수: ${validLots.size}")
                        if (validLots.isNotEmpty()) {
                            MapMarkerManager.clearMarkers()
                            MapMarkerManager.addMarkers(context, map, validLots)
                        }
                    }
                }
            )
            mapView
        }
    )

    LaunchedEffect(kakaoMapState.value, parkingLots) {
        kakaoMapState.value?.let { map ->
            val validLots = parkingLots.map { it.second }
                .filter { it.lat != null && it.lng !=null }

            Log.d("KAKAO_MAP", "마커 추가: 유효 주차장 ${validLots.size}개")
            MapMarkerManager.clearMarkers()
            MapMarkerManager.addMarkers(context, map, validLots)
        } ?: Log.w("KAKAO_MAP", "지도 준비 안됨 → 마커 생략")
    }
}