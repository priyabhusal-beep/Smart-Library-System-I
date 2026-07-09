package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme
import com.sample.smartlibrarysystem.viewmodel.UserViewModel

class RegistrationScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartLibrarySystemTheme {
                RegistrationActivity()
            }
        }
    }
}

@Composable
fun RegistrationActivity() {
    val context = LocalContext.current
    val userViewModel = remember { UserViewModel() }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val primary = Color(0xFF8F1D3F)
    val secondary = Color(0xFFD76C82)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF7FA), Color(0xFFFFE4EC))
                )
            )
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.smartlibrary),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(95.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Create Account",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )

                Text(
                    text = "Register to start borrowing books",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                CommonField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Full Name",
                    icon = R.drawable.baseline_person_24,
                    primary = primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                CommonField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email Address",
                    icon = R.drawable.baseline_email_24,
                    primary = primary,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(14.dp))

                CommonField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Phone Number",
                    icon = R.drawable.baseline_phone_24,
                    primary = primary,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(14.dp))

                PasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    visible = passwordVisible,
                    onVisibilityChange = { passwordVisible = !passwordVisible },
                    primary = primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm Password",
                    visible = confirmPasswordVisible,
                    onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                    primary = primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        userViewModel.register(
                            name = fullName,
                            email = email,
                            phone = phone,
                            password = password,
                            confirmPassword = confirmPassword
                        ) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                context.startActivity(Intent(context, LoginScreen::class.java))
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(primary, secondary)),
                                RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Register", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)

                    TextButton(
                        onClick = {
                            context.startActivity(Intent(context, LoginScreen::class.java))
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Login",
                            color = primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommonField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: Int,
    primary: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primary,
            focusedLabelColor = primary,
            cursorColor = primary
        )
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onVisibilityChange: () -> Unit,
    primary: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_lock_24),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    painter = painterResource(
                        if (visible)
                            R.drawable.baseline_visibility_24
                        else
                            R.drawable.baseline_visibility_off_24
                    ),
                    contentDescription = null
                )
            }
        },
        visualTransformation =
            if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primary,
            focusedLabelColor = primary,
            cursorColor = primary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    SmartLibrarySystemTheme {
        RegistrationActivity()
    }
}