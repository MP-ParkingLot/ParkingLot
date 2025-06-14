// app/src/main/java/com/example/parkinglot/MainActivity.kt
package com.example.parkinglot
import LoginScreen
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.parkinglot.ui.theme.ParkinglotTheme
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkinglot.repository.ParkingLotRepository
import com.example.parkinglot.ui.component.MainScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.parkinglot.ui.theme.ParkingLotTheme
import com.example.parkinglot.viewmodel.ParkingViewModel
import com.example.parkinglot.viewmodel.ParkingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            ParkinglotTheme {
//                AppNavHost()
//            }
//        }
//    }
//}
        // KakaoMapSdk.init 호출은 MyApplication에서 이미 처리되므로 여기서는 제거
        enableEdgeToEdge()
        setContent {
            // ParkingLotRepository 인스턴스 생성
            // RetrofitClient들을 사용하여 Repository를 초기화
            val parkingLotRepository = ParkingLotRepository()

            // ViewModelFactory 생성 및 ViewModel 주입
            val factory = ParkingViewModelFactory(parkingLotRepository)
            val parkingViewModel: ParkingViewModel = viewModel(factory = factory)

            ParkingLotTheme {
                MainScreen(viewModel = parkingViewModel)
            }
        }
    }
    // getMetaDataValue 함수는 더 이상 필요 없으므로 제거
}
