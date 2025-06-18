//app/src/main/java/com/example/parkinglot/auth/AuthClientProvider.kt
package com.example.parkinglot.auth

import android.util.Log
import com.example.parkinglot.ApiService
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthClientProvider {
    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    // 외부에서 토큰 저장을 위해 넘겨줄 수 있도록 콜백 등록
    private var onTokenReceived: ((String) -> Unit)? = null

    fun setTokenCallback(callback: (String) -> Unit) {
        onTokenReceived = callback
    }

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
            cookies.firstOrNull { it.name == "accessToken" }?.value?.let { token ->
                Log.d("AuthClientProvider", "토큰 감지: $token")
                onTokenReceived?.invoke(token)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host].orEmpty()
        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://52.78.160.90/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
