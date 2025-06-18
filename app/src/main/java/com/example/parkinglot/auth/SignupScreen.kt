package com.example.parkinglot.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen() {
    var id by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var idValidationResult by remember { mutableStateOf<String?>(null) }
    var isIdAvailable by remember { mutableStateOf<Boolean?>(null) }

    val isPasswordMatched = confirmPassword.isNotEmpty() && confirmPassword == password

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDDE2EAF6))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 로고 생략
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
                color = if (isIdAvailable == true) Color.Blue else Color.Red,
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: Sign-up logic */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = id.isNotBlank() && nickname.isNotBlank() &&
                    password.isNotBlank() && isPasswordMatched && isIdAvailable == true
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