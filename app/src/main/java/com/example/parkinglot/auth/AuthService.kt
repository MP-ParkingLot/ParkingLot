//app/src/main/java/com/example/parkinglot/dto/network/auth/AuthService.kt

package com.example.parkinglot.auth

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
    fun signup(@Body request: SignupRequest): Call<AuthResponse>
}