//app/src/main/java/com/example/parkinglot/ui/component/VerticalFilterButtons.kt

package com.example.parkinglot.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VerticalFilterButtons(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // 버튼 목록
    val filters = listOf("전체","거리", "빈 자리", "무료")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter

            Button(
                onClick = { onSelectFilter(filter) },
                enabled = isEnabled,
                // 선택된 버튼만 색상을 달리
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else if (isEnabled)
                        Color(0xFFF2F2F2)
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected)
                        Color.White
                    else if (isEnabled)
                        Color.Black
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                // 텍스트가 잘리지 않도록 충분한 패딩을 줌
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = filter)
            }
        }
    }
}
