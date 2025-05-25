package com.example.parkinglot.ui.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun VerticalFilterButtons(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("거리", "빈 자리", "무료")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Button(
                onClick = {
                    onSelectFilter(filter)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray,
                    contentColor = if (isSelected)
                        Color.White
                    else
                        Color.Black
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = filter)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerticalFilterButtonsPreview() {
    var selected by remember { mutableStateOf("거리") }
    VerticalFilterButtons(
        selectedFilter = selected,
        onSelectFilter = { selected = it }
    )
}