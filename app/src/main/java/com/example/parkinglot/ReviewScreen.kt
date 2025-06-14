// app/src/main/java/com/example/parkinglot/viewmodel/ReviewScreen.kt
package com.example.parkinglot

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    locationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToWriteReview: () -> Unit,
    onNavigateToUpdateReview: (Review) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = locationId) {
        viewModel.loadReviewsForLocation(locationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(locationId, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기") } }
            )
        },
        floatingActionButton = {
            if (!uiState.userHasReviewed) {
                FloatingActionButton(onClick = onNavigateToWriteReview) {
                    Icon(Icons.Default.Add, "리뷰 추가")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ReviewHeader(
                averageRate = uiState.averageRate,
                currentSortOrder = uiState.sortOrder,
                selectedFilterTags = uiState.selectedFilterTags,
                onSortClick = { viewModel.setSortOrder(it) },
                onTagClick = { viewModel.toggleFilterTag(it) }
            )
            Divider(modifier = Modifier.padding(top = 16.dp))
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.reviews, key = { it.id }) { review ->
                    ReviewListItem(
                        review = review,
                        onReviewClick = {
                            if (review.isMine) {
                                onNavigateToUpdateReview(review)
                            }
                        },
                        onLikeClick = { viewModel.toggleLike(review.id, review.isLikedByMe) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewHeader(
    averageRate: Float,
    currentSortOrder: SortOrder,
    selectedFilterTags: Set<String>,
    onSortClick: (SortOrder) -> Unit,
    onTagClick: (String) -> Unit
) {
    val availableTags = listOf("넓은 공간", "화장실", "충전소")
    val starColor = Color(0xFFFBC02D)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "%.1f".format(averageRate), fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Star, "평균 별점", tint = starColor, modifier = Modifier.size(32.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth()) {
            availableTags.forEach { tag ->
                FilterChip(
                    selected = selectedFilterTags.contains(tag),
                    onClick = { onTagClick(tag) },
                    label = { Text(tag) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            SortOrder.entries.forEach { order ->
                TextButton(onClick = { onSortClick(order) }) {
                    Text(
                        text = order.displayName,
                        fontWeight = if (currentSortOrder == order) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentSortOrder == order) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewListItem(review: Review, onReviewClick: () -> Unit, onLikeClick: () -> Unit) {
    val starColor = Color(0xFFFBC02D)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onReviewClick, enabled = review.isMine),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = if (review.isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    (1..5).forEach { index ->
                        Icon(Icons.Default.Star, null, tint = if (index <= review.rate) starColor else Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${review.rate}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(review.contents, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    review.categories.filter { it.value }.keys.forEach { tag ->
                        Text("#$tag", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    IconButton(onClick = onLikeClick, modifier = Modifier.size(24.dp)) {
                        Icon(if (review.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "좋아요", tint = if (review.isLikedByMe) Color.Red else Color.Gray)
                    }
                    Text("${review.likes}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
