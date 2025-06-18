//app/src/main/java/com/example/parkinglot/viewmodel/factory/ReviewViewModelFactory.kt

package com.example.parkinglot.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkinglot.data.repository.auth.UserInfo
import com.example.parkinglot.data.repository.review.ReviewRepository
import com.example.parkinglot.viewmodel.review.ReviewViewModel

class ReviewViewModelFactory(
    private val repository: ReviewRepository,
    private val currentUser: UserInfo?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repository, currentUser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}