package com.example.parkinglot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkinglot.uicomponent.KakaoMapScreen
import com.example.parkinglot.ui.component.DistrictDropdownMenu
import com.example.parkinglot.ui.component.FilterRow
import com.example.parkinglot.viewmodel.ParkingViewModel

@Composable
fun MainScreen(viewModel: ParkingViewModel = viewModel()) {
    val uiState by remember { derivedStateOf { viewModel.uiState } }
    val filteredParkingLots by remember { derivedStateOf { viewModel.filteredParkingLots } }

    LaunchedEffect(Unit) {
        if (uiState.parkingLots.isEmpty()) {
            viewModel.fetchParkingLots()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DistrictDropdownMenu(
            onDistrictSelected = { viewModel.updateSelectedDistrict(it) },
            selectedDistrict = viewModel.selectedDistrict
        )

        Spacer(modifier = Modifier.height(8.dp))

        FilterRow(viewModel = viewModel)

        Spacer(modifier = Modifier.height(8.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("오류 발생: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchParkingLots() }) {
                            Text("다시 시도")
                        }
                    }
                }
            }

            else -> {
                KakaoMapScreen(
                    modifier = Modifier.weight(1f),
                    //viewModel = viewModel
                )
            }
        }
    }
}
