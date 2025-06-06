package com.example.parkinglot.dto.response

data class KakaoAddressResponse(
    val documents: List<Document>
) {
    data class Document(
        val address: Address
    ) {
        data class Address(
            val x: String, // 경도
            val y: String  // 위도
        )
    }
}

