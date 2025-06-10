// app/src/main/java/com/example/parkinglot/uicomponent/KakaoMapScreen.kt
package com.example.parkinglot.uicomponent

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.parkinglot.R
import com.example.parkinglot.uistate.CombinedParkingLotInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles // 여전히 필요하지만 사용 방식이 달라집니다.

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun KakaoMapScreen(
    modifier: Modifier = Modifier,
    parkingLots: List<CombinedParkingLotInfo>,
    onMarkerClick: (CombinedParkingLotInfo) -> Unit,
    onLocationUpdate: (Location) -> Unit,
    mapCenterRequest: LatLng?,
    onMapCenterMoveHandled: () -> Unit
) {
    val context = LocalContext.current
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnMapCenterMoveHandled by rememberUpdatedState(onMapCenterMoveHandled)

    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val initialCameraMovedByLocation = remember { mutableStateOf(false) }
    // 주차장 정보 맵은 그대로 유지 (LabelOptions의 ID와 매핑)
    val parkingLotInfoMap = remember { mutableStateOf<Map<String, CombinedParkingLotInfo>>(emptyMap()) }

    // 권한 체크 및 위치 업데이트 로직 (이 부분은 동일)
    val permissionsGranted = permissionsState.permissions.any { it.status.isGranted }
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    DisposableEffect(permissionsGranted) {
        if (!permissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    currentLocation = it
                    onLocationUpdate(it)
                    Log.d("KakaoMapScreen", "위치 업데이트: ${it.latitude}, ${it.longitude}")

                    if (!initialCameraMovedByLocation.value && mapCenterRequest == null) {
                        kakaoMapState.value?.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(it.latitude, it.longitude)))
                        initialCameraMovedByLocation.value = true
                        Log.d("KakaoMapScreen", "초기 위치로 지도 중심 이동 (첫 위치): ${it.latitude}, ${it.longitude}")
                    }
                }
            }
        }

        if (permissionsGranted && (hasFineLocation || hasCoarseLocation)) {
            val req = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
            }.build()
            fusedLocationClient.requestLocationUpdates(
                req, locationCallback, Looper.getMainLooper()
            )
        }
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    val mapView = rememberMapView(context) { kakaoMap ->
        kakaoMapState.value = kakaoMap
        kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(
                map: KakaoMap,
                layer: LabelLayer,
                label: Label
            ): Boolean {
                if (label.labelId == "myCurrentLocation") {
                    Log.d("KakaoMapScreen", "내 위치 마커 클릭됨 (무시)")
                    return true
                }
                parkingLotInfoMap.value[label.labelId]?.let { info ->
                    currentOnMarkerClick(info)
                    // Log.d("KakaoMapScreen", "주차장 클릭됨: ${info.placeName}") // placeName -> LocationID 변경
                    Log.d("KakaoMapScreen", "주차장 클릭됨: ${info.LocationID}") // placeName -> LocationID 변경
                }
                return true
            }
        })
    }

    // 지도 중심 이동 요청 처리 (동일)
    LaunchedEffect(mapCenterRequest, kakaoMapState.value) {
        val map = kakaoMapState.value ?: return@LaunchedEffect
        mapCenterRequest?.let { latLng ->
            map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
            Log.d("KakaoMapScreen", "mapCenterRequest로 지도 중심 이동: $latLng")
            initialCameraMovedByLocation.value = true
            currentOnMapCenterMoveHandled()
        }
    }

    LaunchedEffect(parkingLots, kakaoMapState.value, currentLocation) {
        val map = kakaoMapState.value ?: return@LaunchedEffect
        val manager = map.labelManager ?: return@LaunchedEffect
        val layer = manager.getLayer() ?: return@LaunchedEffect
        layer.removeAll() // 기존 마커 모두 제거

        // ★★★★ 이 부분을 아래와 같이 수정합니다. LabelManager.addLabelStyles()를 호출하지 않습니다. ★★★★
        // 필요한 LabelStyle 객체들을 직접 생성하여 변수에 저장합니다.
        val myLocationStyle = LabelStyle.from(R.drawable.my_location_64).setZoomLevel(0)
        val defaultParkingStyle = LabelStyle.from(R.drawable.marker_gray).setZoomLevel(0)
        val plentyParkingStyle = LabelStyle.from(R.drawable.marker_green).setZoomLevel(0)
        val moderateParkingStyle = LabelStyle.from(R.drawable.marker_yellow).setZoomLevel(0)
        val busyParkingStyle = LabelStyle.from(R.drawable.marker_red).setZoomLevel(0)
        val fullParkingStyle = LabelStyle.from(R.drawable.marker_red).setZoomLevel(0)

        // 1) 내 위치 마커 (현재 위치가 있을 경우)
        currentLocation?.let { loc ->
            val pos = LatLng.from(loc.latitude, loc.longitude)
            val myOpts = LabelOptions.from("myCurrentLocation", pos)
                // LabelStyles.from(단일 LabelStyle 객체)를 사용하여 직접 스타일을 적용합니다.
                .setStyles(LabelStyles.from(myLocationStyle))
            layer.addLabel(myOpts)
            Log.d("KakaoMapScreen", "내 위치 마커 업데이트: ${loc.latitude}, ${loc.longitude}")
        }

        // 2) 주차장 마커
        val newMap = mutableMapOf<String, CombinedParkingLotInfo>()
        parkingLots.forEach { lot ->
            if (lot.latitude != 0.0 && lot.longitude != 0.0) {
                val pos = LatLng.from(lot.latitude, lot.longitude)
                // ratio에 따라 적절한 LabelStyle 객체를 선택합니다.
                val chosenStyle = when (lot.ratio?.uppercase()) {
                    "PLENTY" -> plentyParkingStyle
                    "MODERATE" -> moderateParkingStyle
                    "BUSY" -> busyParkingStyle
                    "FULL" -> fullParkingStyle
                    else -> defaultParkingStyle
                }
                val opts = LabelOptions.from("parking_${lot.id}", pos)
                    // 선택된 단일 LabelStyle 객체로 LabelStyles를 생성하여 적용합니다.
                    .setStyles(LabelStyles.from(chosenStyle))
                newMap["parking_${lot.id}"] = lot
                layer.addLabel(opts)
            } else {
                // Log.w("KakaoMapScreen", "주차장 ${lot.placeName} (${lot.id})의 좌표가 유효하지 않습니다: ${lot.latitude}, ${lot.longitude}") // placeName -> LocationID 변경
                Log.w("KakaoMapScreen", "주차장 ${lot.LocationID} (${lot.id})의 좌표가 유효하지 않습니다: ${lot.latitude}, ${lot.longitude}") // placeName -> LocationID 변경
            }
        }
        parkingLotInfoMap.value = newMap
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView }
    )
}

@Composable
fun rememberMapView(
    context: Context,
    onMapReady: (KakaoMap) -> Unit
): MapView = remember {
    MapView(context).also { mv ->
        mv.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() { Log.d("KakaoMapScreen", "onMapDestroy") }
                override fun onMapError(e: Exception?) { Log.e("KakaoMapScreen", "onMapError: ${e?.message}", e) }
                override fun onMapResumed() { Log.d("KakaoMapScreen", "onMapResumed") }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    Log.d("KakaoMapScreen", "지도 준비 완료 (onMapReady)")
                    onMapReady(map)
                }
            }
        )
    }
}