package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text

val seoulDistricts = listOf(
    "강남구", "강동구", "강북구", "강서구",
    "관악구", "광진구", "구로구", "금천구",
    "노원구", "도봉구", "동대문구", "동작구",
    "마포구", "서대문구", "서초구", "성동구",
    "성북구", "송파구", "양천구", "영등포구",
    "용산구", "은평구", "종로구", "중구", "중랑구"
)

@Composable
fun DistrictDropdownMenu(
    selectedDistrict: String,
    onDistrictSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val districts = seoulDistricts // Constants.kt에 정의된 목록

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedDistrict.ifEmpty { "구 선택" })
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            districts.forEach { district ->
                DropdownMenuItem(
                    text = { Text(district) },
                    onClick = {
                        onDistrictSelected(district)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DistrictDropdownPreview() {
    var selected by remember { mutableStateOf("") }
    DistrictDropdownMenu(selectedDistrict = selected) {
        selected = it
    }
}