//app/src/main/java/com/example/parkinglot/ui/component/map/MapMarkerManager.kt

package com.example.parkinglot.ui.component.map

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import com.example.parkinglot.R
import com.example.parkinglot.domain.model.parking.CombinedParkingLotInfo
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelLayerOptions
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

/**
 * 카카오 벡터 지도에서 주차장 마커를 표시하고 관리하는 싱글톤 객체
 * 이제 CombinedParkingLotInfo의 latitude/longitude를 사용하여 마커를 찍습니다.
 */
object MapMarkerManager {

    private const val TAG = "MapMarkerManager"
    private var layer: LabelLayer? = null

    /** 기존 마커 모두 제거 */
    fun clearMarkers() {
        Log.d(TAG, "마커 초기화 시작")
        layer?.removeAll()
        layer = null
        Log.d(TAG, "마커 매니저 초기화 완료")
    }

    /**
     * CombinedParkingLotInfo 리스트를 기반으로 지도에 마커 추가
     *
     * @param context       앱 컨텍스트 (리소스 접근)
     * @param map           KakaoMap 인스턴스
     * @param lots          CombinedParkingLotInfo 리스트
     * @param onMarkerClick 마커 클릭 시 호출할 콜백
     */
    fun addMarkers(
        context: Context,
        map: KakaoMap,
        lots: List<CombinedParkingLotInfo>,
        onMarkerClick: (CombinedParkingLotInfo) -> Unit
    ) {
        Log.d(TAG, "=== addMarkers 호출, lots.size=${lots.size} ===")

        // 1) 기존 마커 제거
        clearMarkers()

        // 2) 새 레이어 생성
        val labelLayer = map.labelManager?.addLayer(LabelLayerOptions.from())
            ?: run {
                Log.e(TAG, "labelManager.addLayer() 리턴 null 입니다!")
                return
            }
        layer = labelLayer

        // 3) Label → CombinedParkingLotInfo 매핑용
        val labelToInfo = mutableMapOf<Label, CombinedParkingLotInfo>()

        // 4) 각 주차장 정보로 마커 생성
        lots.forEach { info ->
            // API에서 받은 위도/경도
            val pos = LatLng.from(info.latitude, info.longitude)

            // 아이콘 리소스 선택
            @DrawableRes
            val iconRes = when (info.ratio?.uppercase()) {
                "PLENTY"   -> R.drawable.marker_green
                "MODERATE" -> R.drawable.marker_yellow
                "BUSY", "FULL" -> R.drawable.marker_red
                else       -> R.drawable.marker_gray
            }

            // 스타일 생성 & 등록
            val style = LabelStyle.from(iconRes).setZoomLevel(0)
            val styles = map.labelManager
                ?.addLabelStyles(LabelStyles.from(style))
                ?: run {
                    Log.e(TAG, "LabelStyles 등록 실패: ${info.id}")
                    return@forEach
                }

            // 옵션 생성 후 레이어에 추가
            val opts = LabelOptions.from(info.id, pos).setStyles(styles)
            try {
                val label = labelLayer.addLabel(opts)
                if (label != null) {
                    labelToInfo[label] = info
                    Log.d(TAG, "마커 생성 성공: ${info.id} @(${info.latitude},${info.longitude})")
                } else {
                    Log.e(TAG, "LabelLayer.addLabel() 리턴 null (${info.id})")
                }
            } catch (e: Exception) {
                Log.e(TAG, "마커 추가 중 예외 (${info.id})", e)
            }
        }

        // 5) 클릭 리스너 설정
        map.setOnLabelClickListener { _, _, label ->
            labelToInfo[label]?.let { info ->
                onMarkerClick(info)
                return@setOnLabelClickListener true
            }
            false
        }
        labelLayer.isVisible = true

        // 6) 모든 마커를 화면에 보이도록 카메라 이동
        val positions = lots
            .map { LatLng.from(it.latitude, it.longitude) }
            .toTypedArray()
        if (positions.isNotEmpty()) {
            map.moveCamera(
                CameraUpdateFactory.fitMapPoints(positions, 100)
            )
            Log.d(TAG, "카메라 fitMapPoints 호출: 마커 수=${positions.size}")
        } else {
            Log.d(TAG, "유효한 마커 위치 없음, 카메라 이동 생략")
        }
    }
}