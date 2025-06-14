package com.example.parkinglot.auth

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("id") val id: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("message") val message: String
)