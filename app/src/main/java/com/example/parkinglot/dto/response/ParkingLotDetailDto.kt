package com.example.parkinglot.dto.response

import android.util.Log
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "row", strict = false)
data class ParkingLotDetail(
    @field:Element(name = "PKLT_CD", required = false)
    var code: String? = null,

    @field:Element(name = "PKLT_NM", required = false)
    var name: String? = null,

    @field:Element(name = "ADDR", required = false)
    var address: String? = null,

    @field:Element(name = "TPKCT", required = false)
    var tpkct: Int? = null,

    @field:Element(name = "NOW_PRK_VHCL_CNT", required = false)
    var nowPrkVhclCnt: Int? = null,

    @field:Element(name = "BSC_PRK_CRG", required = false)
    var charge: String? = null,

    @field:Element(name = "LAT", required = false)
    var latString: String? = null,

    @field:Element(name = "LNG", required = false)
    var lngString: String? = null,

    var lat: Double? = null,
    var lng: Double? = null
) {
    init {
        // latString과 lngString이 null이 아닐 때만 변환
        latString?.let { latStr ->
            lat = latStr.toDoubleOrNull()
            if (lat == null) {
                Log.w("ParkingLotDetail", "❌ 위도 변환 실패: '$latStr' -> $name")
            }
        }

        lngString?.let { lngStr ->
            lng = lngStr.toDoubleOrNull()
            if (lng == null) {
                Log.w("ParkingLotDetail", "❌ 경도 변환 실패: '$lngStr' -> $name")
            }
        }
    }
}
