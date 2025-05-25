package com.example.parkinglot.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkinglot.dto.response.ParkingLotDetail
import com.example.parkinglot.ui.component.DistrictDropdownMenu
import com.example.parkinglot.ui.component.ParkingLotList
import com.example.parkinglot.ui.component.VerticalFilterButtons

@Composable
fun MainScreen(
    selectedDistrict: String = "",
    selectedFilter: String = "",
    onSelectDistrict: (String) -> Unit = {},
    onSelectFilter: (String) -> Unit = {},
    parkingLots: List<Pair<String, ParkingLotDetail>> = emptyList()
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        DistrictDropdownMenu(
            selectedDistrict = selectedDistrict,
            onDistrictSelected = onSelectDistrict
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(modifier = Modifier.weight(1f))
            VerticalFilterButtons(
                selectedFilter = selectedFilter,
                onSelectFilter = {
                    Log.d("MainScreen", "Clicked: $it")
                    onSelectFilter(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ParkingLotList(parkingLots = parkingLots)
    }
}



@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    var selectedDistrict by remember { mutableStateOf("강남구") }
    var selectedFilter by remember { mutableStateOf("거리") }

    val dummyList = listOf(
        "장소ID_1" to ParkingLotDetail("10", "100", "MODERATE", 2000),
        "장소ID_2" to ParkingLotDetail("50", "80", "PLENTY", 1500),
        "장소ID_3" to ParkingLotDetail("0", "120", "FULL", 5000)
    )

    MainScreen(
        selectedDistrict = selectedDistrict,
        selectedFilter = selectedFilter,
        onSelectDistrict = { selectedDistrict = it },
        onSelectFilter = { selectedFilter = it },
        parkingLots = dummyList
    )
}
