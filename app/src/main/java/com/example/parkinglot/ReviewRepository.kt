package com.example.parkinglot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class ReviewRepository(private val authTokenProvider: () -> String?) {

    private val apiService = RetrofitClient.api
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    fun getReviewStream(): Flow<List<Review>> = _reviews.asStateFlow()

    // 로그인 상태 확인을 위해 authTokenProvider를 계속 사용합니다.
    private fun isLoggedIn(): Boolean {
        return !authTokenProvider().isNullOrBlank()
    }

    suspend fun fetchReviews(locationId: String, currentUserId: Long?) {
        if (!isLoggedIn()) return
        try {
            // apiService 호출 시 token 파라미터 제거
            val response = withContext(Dispatchers.IO) { apiService.getReviews(locationId) }
            if (response.isSuccessful) {
                _reviews.value = response.body()?.map { it.copy(isMine = it.userId == currentUserId) } ?: emptyList()
            } else {
                println("리뷰 불러오기 실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            println("리뷰 불러오기 중 네트워크 오류 발생: ${e.message}")
        }
    }

    suspend fun addReview(locationId: String, request: ReviewUpdateRequest, author: UserInfo) {
        if (!isLoggedIn()) return
        try {
            // apiService 호출 시 token 파라미터 제거
            val response = withContext(Dispatchers.IO) { apiService.addReview(locationId, request) }
            if (response.isSuccessful) {
                fetchReviews(locationId, author.userId)
            } else {
                println("리뷰 추가 실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            println("리뷰 추가 중 네트워크 오류 발생: ${e.message}")
        }
    }

    suspend fun updateReview(reviewId: Long, request: ReviewUpdateRequest, locationId: String, currentUserId: Long?) {
        if (!isLoggedIn()) return
        try {
            // apiService 호출 시 token 파라미터 제거
            val response = withContext(Dispatchers.IO) { apiService.updateReview(reviewId, request) }
            if (response.isSuccessful) {
                fetchReviews(locationId, currentUserId)
            } else {
                println("리뷰 수정 실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            println("리뷰 수정 중 네트워크 오류 발생: ${e.message}")
        }
    }

    suspend fun deleteReview(reviewId: Long, locationId: String, currentUserId: Long?) {
        if (!isLoggedIn()) return
        try {
            // apiService 호출 시 token 파라미터 제거
            val response = withContext(Dispatchers.IO) { apiService.deleteReview(reviewId) }
            if (response.isSuccessful) {
                fetchReviews(locationId, currentUserId)
            } else {
                println("리뷰 삭제 실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            println("리뷰 삭제 중 네트워크 오류 발생: ${e.message}")
        }
    }

    suspend fun toggleLike(reviewId: Long, isLiked: Boolean, locationId: String, currentUserId: Long?) {
        if (!isLoggedIn()) return
        val request = ReviewLikeRequest(isLike = !isLiked)
        try {
            // apiService 호출 시 token 파라미터 제거
            val response = withContext(Dispatchers.IO) { apiService.toggleLike(reviewId, request) }
            if (response.isSuccessful) {
                fetchReviews(locationId, currentUserId)
            } else {
                println("좋아요 실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            println("좋아요 중 네트워크 오류 발생: ${e.message}")
        }
    }
}