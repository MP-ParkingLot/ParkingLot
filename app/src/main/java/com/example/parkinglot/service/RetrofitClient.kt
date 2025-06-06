package com.example.parkinglot.service

import com.example.parkinglot.service.SeoulParkingService
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://openapi.seoul.go.kr:8088/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    val seoulParkingService: SeoulParkingService =
        retrofit.create(SeoulParkingService::class.java)
}
