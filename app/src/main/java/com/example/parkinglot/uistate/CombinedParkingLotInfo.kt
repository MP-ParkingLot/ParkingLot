//app/src/main/java/com/example/parkinglot/uistate/CombinedParkingLotInfo.kt (새로 생성)
package com.example.parkinglot.uistate

data class CombinedParkingLotInfo(
    val id: String,
    val LocationID: String?, // 주차장 고유 식별자 (리뷰 시스템 연동용)
    val addressName: String?, // ★String?
    val roadAddressName: String?, // ★수정: String -> String? (null 가능성)
    val latitude: Double,
    val longitude: Double,
    val empty: Long,
    val total: Long,
    val ratio: String?, // ★수정: String -> String? (서버에서 null 가능성)
    val charge: String?, // ★수정: String -> String? (서버에서 null 가능성)
    val region2depthName: String? // ★추가: "구" 필터링을 위해 추가 (nullable)
)