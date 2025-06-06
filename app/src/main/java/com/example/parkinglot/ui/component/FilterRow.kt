// com/example/parkinglot/ui/component/FilterRow.kt
package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkinglot.viewmodel.ParkingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel 주입용

@Composable
fun FilterRow(
    modifier: Modifier = Modifier,
    viewModel: ParkingViewModel = viewModel() // ViewModel 주입
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { viewModel.applyFilter("빈 자리") }) { // "빈 자리" 필터 적용
            Text("빈자리")
        }

        Button(onClick = { viewModel.applyFilter("무료") }) { // "무료" 필터 적용
            Text("무료")
        }

        // 행정구 필터 버튼
        Button(onClick = { viewModel.applyFilter("행정구") }) { // "행정구" 필터 적용
            Text(viewModel.selectedDistrict.ifEmpty { "행정구" }) // ViewModel의 selectedDistrict 사용
        }

        // "전체" 필터 버튼 추가 (모든 주차장 다시 표시)
        Button(onClick = { viewModel.applyFilter("전체") }) {
            Text("전체")
        }
    }
}