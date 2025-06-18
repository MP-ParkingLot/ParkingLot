//app/src/main/java/com/example/parkinglot/dto/network/auth/AuthService.kt

package com.example.parkinglot.data.network.auth

import com.example.parkinglot.auth.AuthRequest
import com.example.parkinglot.auth.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("auth")
    fun login(@Body request: AuthRequest): Call<AuthResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/signup")
    fun signup(@Body request: AuthRequest): Call<AuthResponse>
}