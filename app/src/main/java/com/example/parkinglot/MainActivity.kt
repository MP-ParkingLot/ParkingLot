package com.example.parkinglot

import LoginScreen
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.parkinglot.ui.theme.ParkingLotTheme
import com.example.parkinglot.uicomponent.KakaoMapScreen
import com.kakao.vectormap.KakaoMapSdk


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appKey = getMetaDataValue("com.kakao.sdk.AppKey")
        KakaoMapSdk.init(this, appKey)

        enableEdgeToEdge()
        setContent {
            ParkingLotTheme {
                KakaoMapScreen()
            }
        }
    }
    private fun getMetaDataValue(key: String): String {
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        Log.d("Key", "Key: ${appInfo.metaData?.getString(key) ?: ""}")
        return appInfo.metaData?.getString(key) ?: throw IllegalStateException("Missing meta-data: $key")
    }

}