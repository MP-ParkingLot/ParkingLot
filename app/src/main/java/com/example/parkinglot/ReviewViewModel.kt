package com.example.parkinglot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val userHasReviewed: Boolean = false,
    val locationId: String = "",
    val averageRate: Float = 0.0f,
    val sortOrder: SortOrder = SortOrder.LATEST,
    val selectedFilterTags: Set<String> = emptySet()
)

enum class SortOrder(val displayName: String) {
    LATEST("최신순"),
    RATING("평점순"),
    LIKES("좋아요순")
}

class ReviewViewModel(
    private val repository: ReviewRepository,
    private val currentUser: UserInfo?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.LATEST)
    private val _selectedFilterTags = MutableStateFlow<Set<String>>(emptySet())

    init {
        viewModelScope.launch {
            combine(repository.getReviewStream(), _sortOrder, _selectedFilterTags) { reviews, sortOrder, filterTags ->
                val filteredReviews = if (filterTags.isEmpty()) {
                    reviews
                } else {
                    reviews.filter { review ->
                        filterTags.all { tagName -> review.categories[tagName] == true }
                    }
                }

                val sortedReviews = when (sortOrder) {
                    SortOrder.LATEST -> filteredReviews.sortedByDescending { it.createdAt }
                    SortOrder.RATING -> filteredReviews.sortedByDescending { it.rate }
                    SortOrder.LIKES -> filteredReviews.sortedByDescending { it.likes }
                }

                val averageRate = if (reviews.isNotEmpty()) {
                    val avg = reviews.map { it.rate }.average().toFloat()
                    (avg * 2).roundToInt() / 2.0f
                } else {
                    0.0f
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        reviews = sortedReviews,
                        userHasReviewed = reviews.any { it.userId == currentUser?.userId },
                        averageRate = averageRate,
                        sortOrder = sortOrder,
                        selectedFilterTags = filterTags
                    )
                }
            }.collect()
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun toggleFilterTag(tag: String) {
        _selectedFilterTags.update { currentTags ->
            if (currentTags.contains(tag)) currentTags - tag else currentTags + tag
        }
    }

    fun loadReviewsForLocation(locationId: String) {
        _uiState.update { it.copy(locationId = locationId) }
        viewModelScope.launch {
            repository.fetchReviews(locationId, currentUser?.userId)
        }
    }

    fun addReview(request: ReviewUpdateRequest) {
        currentUser?.let { user ->
            viewModelScope.launch {
                repository.addReview(_uiState.value.locationId, request, user)
            }
        }
    }

    fun updateReview(reviewId: Long, request: ReviewUpdateRequest) {
        viewModelScope.launch {
            repository.updateReview(reviewId, request, _uiState.value.locationId, currentUser?.userId)
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            repository.deleteReview(reviewId, _uiState.value.locationId, currentUser?.userId)
        }
    }

    fun toggleLike(reviewId: Long, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLike(reviewId, isCurrentlyLiked, _uiState.value.locationId, currentUser?.userId)
        }
    }
}
