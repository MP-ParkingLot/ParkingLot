// app/src/main/java/com/example/parkinglot/viewmodel/ReviewFormScreens.kt
package com.example.parkinglot.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(viewModel: ReviewViewModel, onNavigateBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var rate by remember { mutableIntStateOf(0) }
    var selectedCategories by remember { mutableStateOf(mapOf("넓은 공간" to false, "화장실" to false, "충전소" to false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("리뷰 작성", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            ReviewForm(title, content, rate, selectedCategories, { title = it }, { content = it }, { rate = it }, { category, isSelected ->
                selectedCategories = selectedCategories.toMutableMap().apply { this[category] = isSelected }
            })
            Button(
                onClick = {
                    val request = ReviewUpdateRequest(title, content, rate, selectedCategories)
                    viewModel.addReview(request)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = rate > 0 && content.isNotBlank() && title.isNotBlank()
            ) { Text("리뷰 추가하기") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDeleteScreen(review: Review, viewModel: ReviewViewModel, onNavigateBack: () -> Unit) {
    var title by remember { mutableStateOf(review.title) }
    var content by remember { mutableStateOf(review.contents) }
    var rate by remember { mutableIntStateOf(review.rate) }
    var selectedCategories by remember { mutableStateOf(review.categories) }

    val isChanged = review.title != title || review.contents != content || review.rate != rate || review.categories != selectedCategories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("리뷰 수정", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            ReviewForm(title, content, rate, selectedCategories, { title = it }, { content = it }, { rate = it }, { category, isSelected ->
                selectedCategories = selectedCategories.toMutableMap().apply { this[category] = isSelected }
            })
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.deleteReview(review.id); onNavigateBack() }, modifier = Modifier.weight(1f)) { Text("삭제하기") }
                Button(onClick = {
                    val request = ReviewUpdateRequest(title, content, rate, selectedCategories)
                    viewModel.updateReview(review.id, request)
                    onNavigateBack()
                }, modifier = Modifier.weight(1f), enabled = isChanged) { Text("수정하기") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewForm(
    title: String, content: String, rate: Int, selectedCategories: Map<String, Boolean>,
    onTitleChange: (String) -> Unit, onContentChange: (String) -> Unit, onRateChange: (Int) -> Unit, onCategoryToggle: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            (1..5).forEach { index ->
                IconButton(onClick = { onRateChange(index) }) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(40.dp), tint = if (index <= rate) Color(0xFFFBC02D) else Color.LightGray)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            selectedCategories.keys.sorted().forEach { category ->
                FilterChip(
                    selected = selectedCategories[category] ?: false,
                    onClick = { onCategoryToggle(category, !(selectedCategories[category] ?: false)) },
                    label = { Text(category) }
                )
            }
        }
        OutlinedTextField(value = title, onValueChange = onTitleChange, label = { Text("제목") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = content, onValueChange = onContentChange, label = { Text("리뷰 내용") }, modifier = Modifier.fillMaxWidth().height(150.dp))
    }
}
