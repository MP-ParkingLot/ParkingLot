//app/src/main/java/com/example/parkinglot/dto/network/core/RetrofitClient.kt

package com.example.parkinglot.data.network.core

import com.example.parkinglot.data.network.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.CookieManager

object RetrofitClient {
    private const val BASE_URL = "http://52.78.160.90/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 서버와 통신 시 쿠키를 자동으로 관리할 CookieJar를 생성합니다.
    private val cookieJar: CookieJar = JavaNetCookieJar(CookieManager())

    // OkHttpClient를 빌드할 때 cookieJar를 설정합니다.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .cookieJar(cookieJar)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}