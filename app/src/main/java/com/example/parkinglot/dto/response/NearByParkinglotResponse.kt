package com.example.parkinglot.dto.response

data class NearByParkinglotResponse(
    val parkingLot: Map<String, ParkinglotDetail>
)

data class ParkinglotDetail(
    val empty: String, //빈자리 수 (숫자여서 Int여도 상관없음)
    val total: String, //전체 자리 수 (숫자여서 Int여도 상관없음)
    val ratio: String, //혼잡도
    val charge: Int //비용
)

