// app/src/main/java/com/example/parkinglot/viewmodel/ParkingViewModelFactory.kt
package com.example.parkinglot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkinglot.repository.ParkingLotRepository

class ParkingViewModelFactory(private val repository: ParkingLotRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParkingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParkingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}