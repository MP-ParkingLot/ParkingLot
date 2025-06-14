//app/src/main/java/com/example/parkinglot/auth/AuthViewModel.kt
package com.example.parkinglot.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkinglot.auth.AuthClientProvider
import com.example.parkinglot.auth.AuthRequest
import com.example.parkinglot.auth.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModelFactory(
    private val prefs: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class AuthViewModel(val prefs: SharedPreferences) : ViewModel() {
    var message by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)

    private val api = AuthClientProvider.authService

    init {
        AuthClientProvider.setTokenCallback { token ->
            prefs.edit().putString("token", token).apply()
        }
    }

    fun login(context: Context, id: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = AuthRequest(id, password)
        api.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("로그인 실패: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }


    fun signup(id: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = AuthRequest(id, password)
        api.signup(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                when (response.code()) {
                    201 -> onSuccess()
                    409 -> onError("ID is used")
                    else -> onError("회원가입 실패: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }
}