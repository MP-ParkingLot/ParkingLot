package com.example.parkinglot.uicomponent

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun KakaoMapScreen(modifier: Modifier = Modifier) {
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

    val permissionsGranted = permissionsState.permissions.any { it.status.isGranted }

    val hasFine = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // 요청 및 위치 갱신
    LaunchedEffect(permissionsGranted) {
        permissionsState.launchMultiplePermissionRequest()
        if (permissionsGranted && hasFine && hasCoarse) {
            try {
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()
                locationState.value = location
                Log.d("GPS", "위치 가져옴: ${location.latitude}, ${location.longitude}")
            } catch (e: Exception) {
                Log.e("GPS", "위치 가져오기 실패: ${e.message}")
            }
        }
    }

    val mapView = rememberMapView(context) { kakaoMap ->
        kakaoMapState.value = kakaoMap

        // 지도 준비됐을 때 최초 위치 설정
        locationState.value?.let { location ->
            kakaoMap.moveCamera(
                CameraUpdateFactory.newCenterPosition(
                    LatLng.from(location.latitude, location.longitude)
                )
            )
            Log.d("GPS", "초기 지도 이동 완료")
        }
    }

    // 위치 변경 시 지도 이동
    LaunchedEffect(locationState.value) {
        val location = locationState.value
        val map = kakaoMapState.value
        if (permissionsGranted && location != null && map != null) {
            map.moveCamera(
                CameraUpdateFactory.newCenterPosition(
                    LatLng.from(location.latitude, location.longitude)
                )
            )
            Log.d("GPS", "위치 변경에 따른 지도 이동: ${location.latitude}, ${location.longitude}")
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
