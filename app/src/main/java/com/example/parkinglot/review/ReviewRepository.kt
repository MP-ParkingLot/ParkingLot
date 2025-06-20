//app/src/main/java/com/example/parkinglot/dto/repository/review/ReviewRepository.kt

package com.example.parkinglot.review

import android.util.Log
import com.example.parkinglot.auth.AuthClientProvider
import com.example.parkinglot.auth.SignInInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class ReviewRepository() {

    private val apiService = AuthClientProvider.apiService
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    fun getReviewStream(): Flow<List<Review>> = _reviews.asStateFlow()

    suspend fun fetchReviews(locationId: String, currentUserId: String?) {
        try {
            val response = withContext(Dispatchers.IO) { apiService.getReviews(locationId) }
            if (response.isSuccessful) {
                _reviews.value = response.body()?.map { it.copy(isMine = it.userId == currentUserId) } ?: emptyList()
                Log.d("review--", _reviews.value.toString())
            } else {
                Log.e("리뷰 불러오기 실패", "${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("리뷰 불러오기 중 네트워크 오류 발생", "${e.message}")
        }
    }

    suspend fun addReview(locationId: String, request: ReviewUpdateRequest, author: SignInInfo) {
        try {
            val response =
                withContext(Dispatchers.IO) { apiService.addReview(locationId, request) }
            if (response.isSuccessful) {
                fetchReviews(locationId, author.userId)
            } else {
                Log.e("리뷰 추가 실패", "${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("리뷰 추가 중 네트워크 오류 발생", "${e.message}")
        }
    }

    suspend fun updateReview(reviewId: Long, request: ReviewUpdateRequest, locationId: String, currentUserId: String?) {
        try {
            val response =
                withContext(Dispatchers.IO) { apiService.updateReview(reviewId, request) }
            if (response.isSuccessful) {
                fetchReviews(locationId, currentUserId)
            } else {
                Log.e("리뷰 수정 실패", "${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("리뷰 수정 중 네트워크 오류 발생", "${e.message}")
        }
    }

    suspend fun deleteReview(reviewId: Long, locationId: String, currentUserId: String?) {
        try {
            val response = withContext(Dispatchers.IO) { apiService.deleteReview(reviewId) }
            if (response.isSuccessful) {
                fetchReviews(locationId, currentUserId)
            } else {
                Log.e("리뷰 삭제 실패", "${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("리뷰 삭제 중 네트워크 오류 발생", "${e.message}")
        }
    }

    suspend fun toggleLike(reviewId: Long, isLiked: Boolean, locationId: String, currentUserId: String?) {
        val request = ReviewLikeRequest(isLike = !isLiked)
        try {
            val response =
                withContext(Dispatchers.IO) { apiService.toggleLike(reviewId, request) }
            if (response.isSuccessful) {
                fetchReviews(locationId, currentUserId)
            } else {
                Log.e("좋아요 실패", "${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("좋아요 중 네트워크 오류 발생", "${e.message}")
        }
    }
}