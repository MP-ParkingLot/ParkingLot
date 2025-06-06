package com.example.parkinglot.testdata

import com.example.parkinglot.dto.response.ParkingLotDetail

object SampleParkingLots {
    val list: List<ParkingLotDetail> = listOf(
        ParkingLotDetail(
            code        = "P0001",
            name        = "광화문 주차장",
            address     = "서울 종로구 세종대로 172",
            tpkct       = 120,
            nowPrkVhclCnt = 30,
            charge      = "1000",
            latString   = "37.572388",
            lngString   = "126.976953"
        ),
        ParkingLotDetail(
            code        = "P0002",
            name        = "시청 앞 주차장",
            address     = "서울 중구 세종대로 110",
            tpkct       = 200,
            nowPrkVhclCnt = 150,
            charge      = "1200",
            latString   = "37.566295",
            lngString   = "126.977945"
        ),
        ParkingLotDetail(
            code        = "P0003",
            name        = "남산공원 주차장",
            address     = "서울 중구 소월로 83",
            tpkct       = 80,
            nowPrkVhclCnt = 10,
            charge      = "800",
            latString   = "37.550969",
            lngString   = "126.988215"
        )
    )
}