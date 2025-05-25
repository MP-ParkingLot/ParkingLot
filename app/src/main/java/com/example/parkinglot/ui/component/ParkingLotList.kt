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
                    Text(text = "빈자리: ${detail.empty} / ${detail.total}")
                    Text(text = "혼잡도: ${detail.ratio}")
                    Text(text = "요금: ${detail.charge}원")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingLotListPreview() {
    val sampleData = listOf(
        "장소ID_1" to ParkingLotDetail("190", "200", "BUSY", 3000),
        "장소ID_2" to ParkingLotDetail("50", "100", "MODERATE", 4000),
        "장소ID_3" to ParkingLotDetail("10", "120", "PLENTY", 4500),
        "장소ID_4" to ParkingLotDetail("200", "200", "FULL", 1000)
    )
    ParkingLotList(parkingLots = sampleData)
}
