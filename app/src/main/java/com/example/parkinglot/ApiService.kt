// app/src/main/java/com/example/parkinglot/viewmodel/ApiService.kt
package com.example.parkinglot

import com.example.parkinglot.review.Review
import com.example.parkinglot.review.ReviewLikeRequest
import com.example.parkinglot.review.ReviewUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("review/{locationId}")
    suspend fun getReviews(
        @Header("Authorization") token: String,
        @Path("locationId") locationId: String
    ): Response<List<Review>>

    @POST("review/{locationId}")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Path("locationId") locationId: String,
        @Body request: ReviewUpdateRequest
    ): Response<Unit>

    @PUT("review/{reviewId}")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Long,
        @Body request: ReviewUpdateRequest
    ): Response<Unit>

    @DELETE("review/{reviewId}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Long
    ): Response<Unit>

    @POST("review/{reviewId}/like")
    suspend fun toggleLike(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Long,
        @Body request: ReviewLikeRequest
    ): Response<Unit>
}
