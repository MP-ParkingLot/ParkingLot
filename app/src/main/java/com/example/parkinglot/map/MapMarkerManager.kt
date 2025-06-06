package com.example.parkinglot.map

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import com.example.parkinglot.R
import com.example.parkinglot.dto.response.ParkingLotDetail
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.*

object MapMarkerManager {

    private const val TAG = "MapMarkerManager"
    private var layer: LabelLayer? = null
    private var map: KakaoMap? = null

    fun clearMarkers() {
        Log.d(TAG, "마커 초기화 시작")
        layer?.let {
            it.removeAll()
            Log.d(TAG, "기존 마커 레이어 초기화 완료")
        }
        layer = null
        map = null
        Log.d(TAG, "마커 매니저 초기화 완료")
    }

    fun addMarkers(
        context: Context,
        map: KakaoMap,
        lots: List<ParkingLotDetail>
    ) {
        Log.d(TAG, "마커 추가 시작: ${lots.size}개의 주차장")
        this.map = map

        // 레이어 생성
        val layerOptions = LabelLayerOptions.from()
        layer = map.labelManager?.addLayer(layerOptions)

        // 마커 추가
        lots.forEach { lot ->
            Log.d(TAG, "주차장 마커 추가 시도: ${lot.name}, 위치: ${lot.lat},${lot.lng}")
            lot.lat?.let { lat ->
                lot.lng?.let { lng ->
                    val position = LatLng.from(lat, lng)
                    Log.d(TAG, "마커 위치 생성: $position")

                    try {
                        // iconForRatio 함수를 사용하여 동적으로 아이콘 리소스 ID를 가져옵니다.
                        val iconResId = iconForRatio(lot)
                        // 동적으로 가져온 아이콘 리소스 ID로 스타일을 생성합니다.
                        val style = LabelStyle.from(context, iconResId)

                        val options = LabelOptions.from(lot.name ?: "", position).setStyles(style)
                        val marker = layer?.addLabel(options)
                        if (marker != null) {
                            Log.d(TAG, "마커 추가 성공: ${lot.name}, 위치: $position")
                        } else {
                            Log.e(TAG, "마커 추가 실패: ${lot.name}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "마커 추가 중 오류 발생: ${lot.name}", e)
                    }
                }
            }
        }

        // 레이어 표시 설정
        layer?.isVisible = true
        Log.d(TAG, "레이어 표시 설정 완료: isVisible=${layer?.isVisible}, 마커 수=${layer?.labelCount}")

        // 현재 카메라 위치 로그
        map.cameraPosition?.let { cam ->
            Log.d(TAG, "현재 카메라 위치: ${cam.position}")
        }

        // 마커 추가가 모두 끝난 후, 첫 번째 유효한 마커 위치로 카메라 이동
        val firstLot = lots.firstOrNull { it.lat != null && it.lng != null }
        firstLot?.let {
            val position = LatLng.from(it.lat!!, it.lng!!)
            map.moveCamera(CameraUpdateFactory.newCenterPosition(position, 16))
        }
    }

    @DrawableRes
    private fun iconForRatio(lot: ParkingLotDetail): Int {
        val total = lot.tpkct ?: 0
        val used = lot.nowPrkVhclCnt ?: 0
        val free = (total - used).coerceAtLeast(0)
        // 주차 가능 비율 (남은 공간 / 전체 공간) -> 비율이 낮을수록 주차 공간이 많이 남음
        // 여기서는 ratio를 사용률(used / total)로 계산하여, 사용률이 높을수록 빨간색 마커를 사용하도록 했습니다.
        val ratio = if (total > 0) used.toFloat() / total else 0f // 사용률 (0.0 ~ 1.0)

        return when {
            ratio >= .9f -> R.drawable.marker_red // 90% 이상 사용 (거의 가득 참)
            ratio >= .7f -> R.drawable.marker_yellow // 70% 이상 사용
            ratio >= .4f -> R.drawable.marker_green // 40% 이상 사용
            else -> R.drawable.marker_gray // 그 외 (비어있음)
        }
    }
}