//app/src/main/java/com/example/parkinglot/ui/component/DistrictDropdownMenu.kt

package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview

// 서울 구 목록 (가나다순 정렬)
val seoulDistricts = listOf(
    "강남구", "강동구", "강북구", "강서구",
    "관악구", "광진구", "구로구", "금천구",
    "노원구", "도봉구", "동대문구", "동작구",
    "마포구", "서대문구", "서초구", "성동구",
    "성북구", "송파구", "양천구", "영등포구",
    "용산구", "은평구", "종로구", "중구", "중랑구"
).sorted() // 먼저 구 이름만 정렬

// "구 선택" 옵션을 맨 앞에 추가한 최종 리스트
val seoulDistrictsWithClearOption = listOf("구 선택") + seoulDistricts

@Composable
fun DistrictDropdownMenu(
    selectedDistrict: String,
    onDistrictSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedDistrict.ifEmpty { "구 선택" })
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            seoulDistrictsWithClearOption.forEach { district ->
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