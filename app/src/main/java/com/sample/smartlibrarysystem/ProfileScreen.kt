package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
        containerColor = Color(0xFFF8F7FF)
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { (context as? ComponentActivity)?.finish() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = "Back",
                        tint = Color(0xFFAA3E3E)
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "My Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                if (user?.imageUrl.isNullOrEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_person_24),
                        contentDescription = "Profile picture",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(58.dp)
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

            Spacer(Modifier.height(14.dp))

            Text(
                text = user?.name?.ifEmpty { "No Name" } ?: "No Name",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = user?.email ?: "",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB45454)
                )
            ) {
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    ProfileInfoRow("Full Name", user?.name?.ifEmpty { "N/A" } ?: "N/A")
                    HorizontalDivider()
                    ProfileInfoRow("Email Address", user?.email?.ifEmpty { "N/A" } ?: "N/A")
                    HorizontalDivider()
                    ProfileInfoRow("Phone Number", user?.contact?.ifEmpty { "N/A" } ?: "N/A")
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    userRepo.logOut { success, _ ->
                        if (success) {
                            context.startActivity(Intent(context, LoginScreen::class.java))
                            (context as? ComponentActivity)?.finish()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFDC2626)
                )
            ) {
                Text(
                    text = "Log out",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
    }
}