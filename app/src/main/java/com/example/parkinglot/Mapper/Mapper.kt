package com.example.parkinglot.mapper

import com.example.parkinglot.dto.response.ParkingLotDetail
import com.example.parkinglot.uistate.ParkingLotUiModel

fun Map<String, ParkingLotDetail>.toUiModelList(): List<ParkingLotUiModel> {
    return this.map { (id, detail) ->
        ParkingLotUiModel(
            id = id, //장소 ID
            empty = detail.empty, //빈자리 수
            total = detail.total, //총 자리 수
            ratio = detail.ratio, //혼잡도
            charge = detail.charge, //요금
            isFree = detail.charge == 0 //무료 요금
        )
    }
}