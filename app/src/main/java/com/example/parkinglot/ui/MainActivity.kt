//app/src/main/java/com/example/parkinglot/ui/activity/MainActivity.kt

package com.example.parkinglot.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parkinglot.AppNavHost
import com.example.parkinglot.ui.theme.ParkinglotTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* 시스템 제스처 영역 확장 (옵션) */
        enableEdgeToEdge()

        /* ───────── 앱 UI 렌더링 ───────── */
        setContent {
            ParkinglotTheme {
                /**  ✔ 반드시 NavHost 진입점(AppNavHost)을 사용해야
                 *   리뷰·지도 화면 간 Navigation 이 정상 동작합니다.   */
                AppNavHost()
            }
        }
    }
}