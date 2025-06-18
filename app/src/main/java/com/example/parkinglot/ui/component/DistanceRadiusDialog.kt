// app/src/main/java/com/example/parkinglot/ui/component/DistanceRadiusDialog.kt
package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

/**
 * “거리” 필터용 반경 선택 다이얼로그
 * 0.5 km ~ 10 km, 0.5 단위
 */
@Composable
fun DistanceRadiusDialog(
    currentKm: Float,
    onRadiusSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(currentKm) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onRadiusSelected(sliderValue)
                onDismiss()
            }) { Text("확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
        title = { Text("반경 선택") },
        text = {
            Column {
                Text(String.format("현재 선택: %.1f km", sliderValue))
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0.5f..10f,
                    steps = ((10f - 0.5f) / 0.5f - 1).toInt()   // 0.5 km step
                )
            }
        }
    )
}
