package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkinglot.dto.response.ParkingLotDetail

@Composable
fun ParkingLotList(parkingLots: List<Pair<String, ParkingLotDetail>>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(parkingLots) { (id, detail) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "장소 ID: $id", style = MaterialTheme.typography.titleSmall)
                    Text(text = "주차장명: ${detail.name ?: "정보 없음"}")
                    Text(text = "위치: ${detail.address ?: "주소 정보 없음"}")
                    Text(text = "빈자리: ${detail.nowPrkVhclCnt ?: "알 수 없음"} / ${detail.tpkct ?: "알 수 없음"}")
                    Text(text = "요금: ${detail.charge ?: "요금 정보 없음"}원")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingLotListPreview() {
    val sampleData = listOf(
        "장소ID_1" to ParkingLotDetail(
            code = "190",
            name = "세종로 주차장",
            address = "서울 종로구",
            tpkct = 200,
            nowPrkVhclCnt = 190,
            charge = "3000",
            latString = "37.5665",
            lngString = "126.9780"
        ),
        "장소ID_2" to ParkingLotDetail(
            code = "50",
            name = "종묘 주차장",
            address = "서울 종로구",
            tpkct = 100,
            nowPrkVhclCnt = 50,
            charge = "4000",
            latString = "37.5651",
            lngString = "126.9895"
        )
    )
    ParkingLotList(parkingLots = sampleData)
}
