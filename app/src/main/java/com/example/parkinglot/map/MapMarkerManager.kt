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
        val layerOptions = LabelLayerOptions.from()
        val labelLayer = map.labelManager?.addLayer(layerOptions)

        lots.forEach { lot ->
            val lat = lot.lat
            val lng = lot.lng

            if (lat != null && lng != null) {
                val position = LatLng.from(lat, lng)

                try {
                    val iconResId = iconForRatio(lot)
                    val style = LabelStyle.from(context, iconResId)
                    val options = LabelOptions.from(position).setStyles(style)
                    labelLayer?.addLabel(options)
                } catch (e: Exception) {
                    Log.e(TAG, "마커 추가 중 오류 발생: ${lot.name}", e)
                }
            }
        }

        labelLayer?.isVisible = true

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
        val ratio = if (total > 0) used.toFloat() / total else 0f

        return when {
            ratio >= .9f -> R.drawable.marker_red
            ratio >= .7f -> R.drawable.marker_yellow
            ratio >= .4f -> R.drawable.marker_green
            else -> R.drawable.marker_gray
        }
    }
}