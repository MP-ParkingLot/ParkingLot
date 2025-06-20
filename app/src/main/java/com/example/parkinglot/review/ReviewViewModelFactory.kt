//app/src/main/java/com/example/parkinglot/viewmodel/factory/ReviewViewModelFactory.kt

package com.example.parkinglot.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkinglot.auth.SignInInfo
import com.example.parkinglot.auth.UserInfo

class ReviewViewModelFactory(
    private val repository: ReviewRepository,
    private val currentUser: SignInInfo?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repository, currentUser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}