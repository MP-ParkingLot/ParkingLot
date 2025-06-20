//app/src/main/java/com/example/parkinglot/util/Constants.kt

package com.example.parkinglot.util

import com.kakao.vectormap.LatLng

object Constants {

    // 서울 각 구의 대략적인 중심 좌표
    val SEOUL_DISTRICT_CENTERS: Map<String, LatLng> = mapOf(
        "강남구" to LatLng.from(37.5172, 127.0473),
        "강동구" to LatLng.from(37.5304, 127.1238),
        "강북구" to LatLng.from(37.6415, 127.0163),
        "강서구" to LatLng.from(37.5509, 126.8496),
        "관악구" to LatLng.from(37.4784, 126.9517),
        "광진구" to LatLng.from(37.5383, 127.0827),
        "구로구" to LatLng.from(37.4954, 126.8874),
        "금천구" to LatLng.from(37.4571, 126.8953),
        "노원구" to LatLng.from(37.6542, 127.0568),
        "도봉구" to LatLng.from(37.6688, 127.0471),
        "동대문구" to LatLng.from(37.5744, 127.0396),
        "동작구" to LatLng.from(37.5124, 126.9392),
        "마포구" to LatLng.from(37.5663, 126.9015),
        "서대문구" to LatLng.from(37.5794, 126.9367),
        "서초구" to LatLng.from(37.4836, 127.0326),
        "성동구" to LatLng.from(37.5636, 127.0366),
        "성북구" to LatLng.from(37.5894, 127.0167),
        "송파구" to LatLng.from(37.5145, 127.1065),
        "양천구" to LatLng.from(37.5173, 126.8666),
        "영등포구" to LatLng.from(37.5262, 126.9006),
        "용산구" to LatLng.from(37.5323, 126.9902),
        "은평구" to LatLng.from(37.6027, 126.9292),
        "종로구" to LatLng.from(37.5701, 126.9769),
        "중구" to LatLng.from(37.5636, 126.9979),
        "중랑구" to LatLng.from(37.5953, 127.0939)
    )
}