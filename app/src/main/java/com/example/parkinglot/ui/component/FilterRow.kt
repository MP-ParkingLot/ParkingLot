package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.viewmodel.ParkingViewModel

@Composable
fun FilterRow(viewModel: ParkingViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            // 예시: 빈자리 있는 주차장 조회
            viewModel.fetchEmptyParkingLots(
                NearByParkinglotRequest(parkingLot = listOf()) // 실제 요청 내용 맞게 바꾸기
            )
        }) {
            Text("빈자리")
        }

        Button(onClick = {
            // 예시: 무료 주차장 조회
            viewModel.fetchFreeParkingLots(
                NearByParkinglotRequest(parkingLot = listOf()) // 실제 요청 내용 맞게 바꾸기
            )
        }) {
            Text("무료")
        }

        Button(onClick = {
            // 예시: 강남구 기준으로 행정구 필터 조회
            viewModel.fetchRegionParkingLots("강남구") // 추후 Dropdown으로 바꾸는 것도 가능
        }) {
            Text("행정구")
        }
    }
}
