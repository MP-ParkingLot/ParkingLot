// app/src/main/java/com/example/parkinglot/viewmodel/ReviewData.kt
package com.example.parkinglot.review

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Review(
    val id: Long,
    val title: String,
    val contents: String,
    val rate: Int,
    var likes: Int,
    val createdAt: String,
    val categories: @RawValue Map<String, Boolean>,
    val userId: String,
    val nickname: String,
    val isMine: Boolean = false,
    var isLikedByMe: Boolean = false
) : Parcelable

data class ReviewUpdateRequest(
    val title: String,
    val contents: String,
    val rate: Int,
    val categories: Map<String, Boolean>
)

data class ReviewLikeRequest(
    val isLike: Boolean
)
