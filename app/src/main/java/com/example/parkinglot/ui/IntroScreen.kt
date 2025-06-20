package com.example.parkinglot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.parkinglot.R
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(onNavigateToLogin: ()->Unit={}) {
    LaunchedEffect(Unit) {
        delay(1000)
        onNavigateToLogin()
    }

    Image(
        painter = painterResource(id = R.drawable.intro_screen), // 로고 이미지
        contentDescription = "Intro Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}