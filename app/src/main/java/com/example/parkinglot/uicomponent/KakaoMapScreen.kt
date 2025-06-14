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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.parkinglot.R
import com.example.parkinglot.uistate.CombinedParkingLotInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun KakaoMapScreen(
    modifier: Modifier = Modifier,
    parkingLots: List<CombinedParkingLotInfo>,
    onMarkerClick: (CombinedParkingLotInfo) -> Unit,
    onLocationUpdate: (Location) -> Unit,
    mapCenterRequest: LatLng?,
    onMapCenterMoveHandled: () -> Unit = {}
) {
    /* ────────── remember 영역 ────────── */
    val context                     = LocalContext.current
    val currentOnMarkerClick        by rememberUpdatedState(onMarkerClick)
    val currentOnMapCenterHandled   by rememberUpdatedState(onMapCenterMoveHandled)

    val kakaoMapState               = remember { mutableStateOf<KakaoMap?>(null) }
    val parkingLotInfoMap           = remember { mutableStateOf<Map<String, CombinedParkingLotInfo>>(emptyMap()) }
    var currentLocation             by remember { mutableStateOf<Location?>(null) }
    val firstCameraMovedByLocation  = remember { mutableStateOf(false) }

    /* ────────── 위치 권한 및 업데이트 ────────── */
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val permissionsGranted = permissionState.permissions.any { it.status.isGranted }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    DisposableEffect(permissionsGranted) {
        if (!permissionsGranted) permissionState.launchMultiplePermissionRequest()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                currentLocation = loc
                onLocationUpdate(loc)
                Log.d("KakaoMapScreen", "📍 위치: ${loc.latitude}, ${loc.longitude}")

                if (!firstCameraMovedByLocation.value && mapCenterRequest == null) {
                    kakaoMapState.value?.moveCamera(
                        CameraUpdateFactory.newCenterPosition(
                            LatLng.from(loc.latitude, loc.longitude)
                        )
                    )
                    firstCameraMovedByLocation.value = true
                    Log.d("KakaoMapScreen", "🗺️ 최초 카메라 이동")
                }
            }
        }

        if (permissionsGranted) {
            val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
                .setMinUpdateIntervalMillis(2_000L).build()
            fusedLocationClient.requestLocationUpdates(req, callback, Looper.getMainLooper())
        }
        onDispose { fusedLocationClient.removeLocationUpdates(callback) }
    }

    /* ────────── MapView & Ready 콜백 ────────── */
    val mapView = rememberMapView(context) { map ->
        kakaoMapState.value = map

        map.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(map: KakaoMap, layer: LabelLayer, label: Label): Boolean {
                if (label.labelId == "myCurrentLocation") return true
                parkingLotInfoMap.value[label.labelId]?.let { info ->
                    currentOnMarkerClick(info)
                    Log.d("KakaoMapScreen", "🅿️ 클릭: ${info.LocationID}")
                }
                return true
            }
        })
    }

    /* ────────── 외부 Center 이동 요청 ────────── */
    LaunchedEffect(mapCenterRequest, kakaoMapState.value) {
        val map = kakaoMapState.value ?: return@LaunchedEffect
        mapCenterRequest?.let { latLng ->
            map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
            firstCameraMovedByLocation.value = true
            currentOnMapCenterHandled()
            Log.d("KakaoMapScreen", "🗺️ 외부 카메라 이동: $latLng")
        }
    }

    /* ────────── 마커 갱신 ────────── */
    LaunchedEffect(parkingLots, kakaoMapState.value, currentLocation) {
        val map     = kakaoMapState.value ?: return@LaunchedEffect
        val manager = map.labelManager ?: return@LaunchedEffect
        val layer   = manager.getLayer() ?: return@LaunchedEffect
        layer.removeAll()

        /* 스타일 준비 */
        val styleMe     = LabelStyle.from(R.drawable.my_location_64).setZoomLevel(0)
        val styleGray   = LabelStyle.from(R.drawable.marker_gray).setZoomLevel(0)
        val styleGreen  = LabelStyle.from(R.drawable.marker_green).setZoomLevel(0)
        val styleYellow = LabelStyle.from(R.drawable.marker_yellow).setZoomLevel(0)
        val styleRed    = LabelStyle.from(R.drawable.marker_red).setZoomLevel(0)

        /* 내 위치 */
        currentLocation?.let { loc ->
            val opt = LabelOptions.from(
                "myCurrentLocation",
                LatLng.from(loc.latitude, loc.longitude)
            ).setStyles(LabelStyles.from(styleMe))
            layer.addLabel(opt)
        }

        /* 주차장 */
        val newMap = mutableMapOf<String, CombinedParkingLotInfo>()
        parkingLots.forEach { lot ->
            if (lot.latitude == 0.0 && lot.longitude == 0.0) return@forEach

            val style = when (lot.ratio?.uppercase()) {
                "PLENTY"   -> styleGreen
                "MODERATE" -> styleYellow
                "BUSY", "FULL" -> styleRed
                else       -> styleGray
            }

            val opt = LabelOptions.from(
                "parking_${lot.id}",
                LatLng.from(lot.latitude, lot.longitude)
            ).setStyles(LabelStyles.from(style))

            layer.addLabel(opt)
            newMap["parking_${lot.id}"] = lot
        }
        parkingLotInfoMap.value = newMap
    }

    /* ────────── 실제 MapView 렌더 ────────── */
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory  = { mapView }
    )
}

/* rememberMapView : MapView 생성 헬퍼 */
@Composable
fun rememberMapView(
    context: Context,
    onMapReady: (KakaoMap) -> Unit
): MapView = remember {
    MapView(context).also { mv ->
        mv.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() { /* no-op */ }
                override fun onMapResumed() { /* no-op */ }
                override fun onMapError(e: Exception?) {
                    Log.e("KakaoMapScreen", "MapError: ${e?.message}", e)
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) = onMapReady(map)
            }
        )
    }
}
