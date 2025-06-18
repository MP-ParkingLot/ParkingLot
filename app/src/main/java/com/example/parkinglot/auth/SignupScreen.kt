package com.example.parkinglot.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkinglot.R

@Composable
fun SignUpScreen(onNavigateBack: ()->Unit = {}) {
    var id by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var idValidationResult by remember { mutableStateOf<String?>(null) }
    var isIdAvailable by remember { mutableStateOf<Boolean?>(null) }

    val isPasswordMatched = confirmPassword.isNotEmpty() && confirmPassword == password

    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }
    val factory = remember { AuthViewModelFactory(prefs) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDDE2EAF6))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.width(120.dp).height(111.dp).padding(end = 45.dp, bottom = 20.dp).align(Alignment.Start)) {
            Image(
                painter = painterResource(R.drawable.baseline_location_pin_24),
                contentDescription = null,
                modifier = Modifier.size(50.dp).align(Alignment.TopCenter),
                colorFilter = ColorFilter.tint(Color(0xFE, 0xF7, 0xFF))
            )
            Image(
                painter = painterResource(R.drawable.baseline_directions_car_24),
                contentDescription = null,
                modifier = Modifier.size(50.dp).align(Alignment.BottomCenter)
            )
        }
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("Id") },
            trailingIcon = {
                if (id.isNotEmpty()) {
                    IconButton(onClick = { id = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.width(8.dp))

        // ID 결과 텍스트
        idValidationResult?.let {
            Text(
                text = it,
                color = if (isIdAvailable == true) Color.Black else Color.Red,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nickname
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Nickname") },
            trailingIcon = {
                if (nickname.isNotEmpty()) {
                    IconButton(onClick = { nickname = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (password.isNotEmpty()) {
                    IconButton(onClick = { password = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password + 실시간 체크 아이콘
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Verify Password") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (confirmPassword.isNotEmpty()) {
                    if (isPasswordMatched) {
                        Icon(Icons.Default.Check, contentDescription = "Match", tint = Color.Green)
                    } else {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Not match",
                            tint = Color.Red
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                viewModel.signup(id, password,
                    onSuccess = {
                        isIdAvailable = true
                        Toast.makeText(context, "회원가입 성공! 로그인해주세요.", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    onError = {
                        isIdAvailable = false;
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = id.isNotBlank() && nickname.isNotBlank() &&
                    password.isNotBlank() && isPasswordMatched
        ) {
            Text("Sign-up")
        }
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen()
}