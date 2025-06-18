// app/src/main/java/com/example/parkinglot/viewmodel/AuthManager.kt
package com.example.parkinglot.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthManager {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

//    var accessToken: String? = null
//        private set

    /**
     * TODO (로그인 담당자):
     * - 로그인 성공 시, 서버로부터 받은 액세스 토큰과 사용자 정보를 사용하여
     * 이 onLoginSuccess 함수를 호출해주세요.
     */
    fun onLoginSuccess(userInfo: UserInfo) {
//        accessToken = token
        _currentUser.value = userInfo
    }

    /**
     * TODO (로그인 담당자):
     * - 로그아웃 기능 구현 시, 이 함수를 호출하여 저장된 모든 인증 정보를
     * 깨끗하게 초기화해주세요.
     */
    fun logout() {
//        accessToken = null
        _currentUser.value = null
    }
}

// user 정보에 닉네임은 없어 지웠습니다
data class UserInfo(val userId: String)
