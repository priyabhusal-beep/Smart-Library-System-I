package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.sample.smartlibrarysystem.model.UserModel
import com.sample.smartlibrarysystem.repo.UserRepoImp
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme

class ProfileScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartLibrarySystemTheme {
                ProfileScreenActivity()
            }
        }
    }
}

@Composable
fun ProfileScreenActivity() {
    val context = LocalContext.current
    val userRepo = remember { UserRepoImp() }
    var user by remember { mutableStateOf<UserModel?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            userRepo.getUserById(userId) { success, _, fetchedUser ->
                if (success) user = fetchedUser
                loading = false
            }
        } else {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = "Back"
                    )
                }
                Text("My Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_settings_24),
                        contentDescription = "Settings"
                    )
                }
            }
        },
        containerColor = Color(0xFFF8F7FF)
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))

            // Profile picture or blank circle icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                if (user?.imageUrl.isNullOrEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_person_24),
                        contentDescription = "Profile picture",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(56.dp)
                    )
                } else {
                    AsyncImage(
                        model = user?.imageUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = user?.name?.ifEmpty { "No Name" } ?: "No Name",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF111827)
            )

            Text(
                text = user?.email ?: "",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45454))
            ) {
                Text("Edit Profile")
            }

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                ProfileInfoRow("Full Name", user?.name?.ifEmpty { "N/A" } ?: "N/A")
                Divider()
                ProfileInfoRow("Email Address", user?.email?.ifEmpty { "N/A" } ?: "N/A")
                Divider()
                ProfileInfoRow("Phone Number", user?.contact?.ifEmpty { "N/A" } ?: "N/A")

            }

            Spacer(Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    userRepo.logOut { success, _ ->
                        if (success) {
                            context.startActivity(Intent(context, LoginScreen::class.java))
                            (context as? ComponentActivity)?.finish()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
            ) {
                Text("Log out")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    SmartLibrarySystemTheme {
        ProfileScreenActivity()
    }
}