// com/example/parkinglot/uicomponent/KakaoMapScreen.kt
package com.example.parkinglot.uicomponent

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.parkinglot.map.MapMarkerManager
import com.example.parkinglot.viewmodel.ParkingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun KakaoMapScreen(
    modifier: Modifier = Modifier,
    viewModel: ParkingViewModel = viewModel() // ViewModel 주입
) {
    val context = LocalContext.current

    // ViewModel의 filteredParkingLots를 관찰합니다.
    // ParkingViewModel에서 filteredParkingLots가 `mutableStateOf`이므로 `getValue` 사용
    val parkingLots by viewModel.filteredParkingLots

    val perms = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val fused by remember { mutableStateOf(LocationServices.getFusedLocationProviderClient(context)) }
    val locationState = remember { mutableStateOf<Location?>(null) }

    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }

    val mapView = remember { MapView(context) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            mapView.start(
                object : MapLifeCycleCallback() {
                    override fun onMapResumed() {}
                    override fun onMapDestroy() {}
                    override fun onMapError(p0: Exception?) {
                        Log.e("KAKAO_MAP", "지도 오류", p0)
                    }
                },
                object : KakaoMapReadyCallback() {
                    override fun onMapReady(map: KakaoMap) {
                        Log.d("KAKAO_MAP", "지도 준비 완료")
                        kakaoMapState.value = map

                        // 지도 준비 완료 시 마커 추가 (parkingLots가 이미 데이터 가지고 있을 경우)
                        // LaunchEffect를 사용하여 parkingLots 변경 감지 후 마커 업데이트
                        if (parkingLots.isNotEmpty()) {
                            Log.d("KAKAO_MAP", "초기 마커 추가 시작: ${parkingLots.size}개의 주차장")
                            MapMarkerManager.clearMarkers()
                            MapMarkerManager.addMarkers(
                                context = context,
                                map = map,
                                lots = parkingLots.map { it.second } // ParkingLotDetail 목록 전달
                            )
                            // 첫 번째 마커 위치로 카메라 이동 로직은 MapMarkerManager에서 이미 처리
                        } else {
                            Log.w("KAKAO_MAP", "초기에 추가할 주차장이 없음 (ViewModel 로딩 중일 수 있음)")
                        }
                    }
                }
            )
            mapView
        }
    )

    // parkingLots 데이터가 변경될 때마다 마커를 업데이트합니다.
    LaunchedEffect(parkingLots) {
        val map = kakaoMapState.value
        if (map != null && parkingLots.isNotEmpty()) {
            Log.d("KAKAO_MAP", "주차장 데이터 변경 감지, 마커 업데이트 시작: ${parkingLots.size}개")
            MapMarkerManager.clearMarkers() // 기존 마커 모두 삭제
            MapMarkerManager.addMarkers(
                context = context,
                map = map,
                lots = parkingLots.map { it.second } // ParkingLotDetail 목록 전달
            )
        } else if (map != null && parkingLots.isEmpty()) {
            Log.d("KAKAO_MAP", "주차장 데이터가 비어있음, 마커 초기화.")
            MapMarkerManager.clearMarkers()
        }
    }

    DisposableEffect(perms.permissions) {
        perms.launchMultiplePermissionRequest()

        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            val req = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5_000L)
                .setMinUpdateIntervalMillis(2_000L)
                .build()

            val cb = object : LocationCallback() {
                override fun onLocationResult(res: LocationResult) {
                    locationState.value = res.lastLocation
                }
            }
            fused.requestLocationUpdates(req, cb, Looper.getMainLooper())
            onDispose { fused.removeLocationUpdates(cb) }
        } else onDispose { }
    }
}