
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.parkinglot.auth.AuthViewModel
import com.example.parkinglot.auth.AuthViewModelFactory

@Composable
fun LoginScreen() {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }
    val factory = remember { AuthViewModelFactory(prefs) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xDDE2EAF6)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.width(180.dp).height(140.dp).padding(end = 45.dp)) {
            Image(
                painter = painterResource(R.drawable.baseline_location_pin_24),
                contentDescription = null,
                modifier = Modifier.size(99.dp).align(Alignment.TopStart),
                colorFilter = ColorFilter.tint(Color(0xFE, 0xF7, 0xFF))
            )
            Image(
                painter = painterResource(R.drawable.baseline_directions_car_24),
                contentDescription = null,
                modifier = Modifier.size(77.dp).align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

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
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

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
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Button(onClick = {
                    viewModel.login(context, id, password,
                        onSuccess = {
                            Toast.makeText(context, "로그인 성공! token: " + viewModel.prefs.getString("token", ""), Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                colors = ButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color(0xFE, 0xF7, 0xFF),
                    disabledContainerColor = Color(0xFE, 0xF7, 0xFF),
                    disabledContentColor = Color.Black
                )
            ) {
                Text("Sign-in")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                    viewModel.signup(id, password,
                        onSuccess = {
                            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                colors = ButtonColors(
                    containerColor = Color(0xFE, 0xF7, 0xFF),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFE, 0xF7, 0xFF),
                    disabledContentColor = Color.Black
                )
            ) {
                Text("Sign-up")
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}