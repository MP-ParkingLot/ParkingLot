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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.parkinglot.R
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
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun KakaoMapScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationState = remember { mutableStateOf<Location?>(null) }
    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val initialCameraMoved = remember { mutableStateOf(false) }

    val permissionsGranted = permissionsState.permissions.any { it.status.isGranted }

    val hasFine = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // 요청 및 위치 갱신
    DisposableEffect(permissionsGranted) {
        permissionsState.launchMultiplePermissionRequest()
        if (permissionsGranted && (hasFine || hasCoarse)) {
            // LocationRequest 생성 (5초 간격, 고정밀도)
//            val locationRequest = LocationRequest.create().apply {
//                interval = 5000L
//                fastestInterval = 2000L
//                priority = Priority.PRIORITY_HIGH_ACCURACY
//            }
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
            }.build()

            // 콜백 정의
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        locationState.value = it
                        Log.d("GPS", "위치 업데이트: ${it.latitude}, ${it.longitude}")
                    }
                }
            }

            // 위치 업데이트 요청
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Composable이 dispose될 때 위치 업데이트 해제
            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } else {
            onDispose {  }
        }
    }

    val mapView = rememberMapView(context) { kakaoMap ->
        kakaoMapState.value = kakaoMap

        // 지도 준비됐을 때 최초 위치 설정
//        locationState.value?.let { location ->
//            kakaoMap.moveCamera(
//                CameraUpdateFactory.newCenterPosition(
//                    LatLng.from(location.latitude, location.longitude)
//                )
//            )
//            Log.d("GPS", "초기 지도 이동 완료")
//
//
//            if(kakaoMap.labelManager != null) {
//                Log.d("labelManager", "Not null")
//                val styles = kakaoMap.labelManager?.addLabelStyles(
//                    LabelStyles.from(LabelStyle.from(R.drawable.here_24))
//                )
//                val options = LabelOptions.from(
//                    LatLng.from(location.latitude, location.longitude)
//                ).setStyles(styles)
//                val layer = kakaoMap.labelManager?.getLayer()
//                layer?.addLabel(options)
//            } else {
//                Log.d("labelManager", "Null")
//            }
//        }
    }

    // 위치 변경 시 레이블 이동
    LaunchedEffect(locationState.value) {
        val location = locationState.value
        val map = kakaoMapState.value
        if (permissionsGranted && location != null && map != null) {
            if(initialCameraMoved.value == false)
            {
                map.moveCamera(
                    CameraUpdateFactory.newCenterPosition(
                        LatLng.from(location.latitude, location.longitude)
                    )
                )
                initialCameraMoved.value = true
                Log.d("GPS", "위치 변경에 따른 지도 이동: ${location.latitude}, ${location.longitude}")
            }

            if(map.labelManager != null) {
                val layer = map.labelManager?.getLayer()
                layer?.removeAll()
                val styles = map.labelManager?.addLabelStyles(
                    LabelStyles.from(LabelStyle.from(R.drawable.my_location_64))
                )
                val options = LabelOptions.from(
                    LatLng.from(location.latitude, location.longitude)
                ).setStyles(styles)
                if(layer != null) {
                    layer.addLabel(options)
                    Log.d("layer", "not null")
                } else {
                    Log.d("layer", "null")
                }
            } else {
                Log.d("labelManager", "Null")
            }
        }
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
): MapView {
    return remember {
        MapView(context).also { mapView ->
            mapView.start(
                object : MapLifeCycleCallback() {
                    override fun onMapDestroy() = Unit
                    override fun onMapError(e: Exception?) = Unit
                    override fun onMapResumed() = Unit
                },
                object : KakaoMapReadyCallback() {
                    override fun onMapReady(map: KakaoMap) {
                        Log.d("KAKAO_MAP", "지도 준비 완료")
                        onMapReady(map)
                    }
                }
            )
        }
    }
}
