package com.example.parkinglot.map

import android.util.Log
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelOptions
import com.example.parkinglot.dto.response.ParkingLotDetail

object MapMarkerManager {

    fun addMarkers(
        kakaoMap: KakaoMap,
        parkingLots: List<Pair<String, ParkingLotDetail>>
    ) {
        val labelLayer = kakaoMap.labelManager?.labelLayer
        if (labelLayer == null) {
            Log.e("MapMarkerManager", "LabelLayer is null. 마커를 추가할 수 없음")
            return
        }

        labelLayer.clear() // 기존 마커 제거

        for ((id, detail) in parkingLots) {
            val lat = detail.lat ?: continue
            val lng = detail.lng ?: continue

            val labelOptions = LabelOptions.from(LatLng.from(lat, lng))
                .setTag(id.hashCode()) // 마커 식별 태그
                .setTexts(detail.ratio) // 예: 혼잡도 표시
                .setStyles(getStyleByRatio(detail.ratio)) // 혼잡도별 색상 적용

            labelLayer.addLabel(labelOptions)
        }
    }

    private fun getStyleByRatio(ratio: String): Int {
        return when (ratio.uppercase()) {
            "BUSY" -> R.drawable.marker_red
            "MODERATE" -> R.drawable.marker_yellow
            "PLENTY" -> R.drawable.marker_green
            else -> R.drawable.marker_gray
        }
    }
}
