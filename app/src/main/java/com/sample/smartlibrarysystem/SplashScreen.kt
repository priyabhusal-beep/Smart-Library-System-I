package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme

class SplashScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Show splash for 3 seconds, then open LoginScreen
        Handler(Looper.getMainLooper()).postDelayed({

            // Open Login Screen
            startActivity(Intent(this, LoginScreen::class.java))

            // Close Splash Screen
            finish()

        }, 3000)

        setContent {
            SmartLibrarySystemTheme {
                SplashScreenUI()
            }
        }
    }
}

@Composable
fun SplashScreenUI() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7))
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.smartlibrary),
                    contentDescription = "Library Logo",
                    modifier = Modifier.padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Library Management System",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart Library Management",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SmartLibrarySystemTheme {
        SplashScreenUI()
    }
}