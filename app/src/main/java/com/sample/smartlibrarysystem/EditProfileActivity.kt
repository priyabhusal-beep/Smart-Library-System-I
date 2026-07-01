package com.sample.smartlibrarysystem

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.sample.smartlibrarysystem.model.UserModel
import com.sample.smartlibrarysystem.repo.ImageRepoImp
import com.sample.smartlibrarysystem.repo.UserRepoImp
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme
import com.sample.smartlibrarysystem.utils.ImageUtils
import com.sample.smartlibrarysystem.viewmodel.ImageViewModel

class EditProfileActivity : ComponentActivity() {

    private val imageViewModel = ImageViewModel(ImageRepoImp())
    private val userRepo = UserRepoImp()
    private lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var onImageUploaded: (String?) -> Unit = {}

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            if (uri != null) {
                imageViewModel.uploadImage(this, uri) { success, result ->
                    if (success) {
                        onImageUploaded(result)
                    }
                }
            }
        }

        setContent {
            SmartLibrarySystemTheme {
                EditProfileScreen(
                    onChangePhotoClick = { imageUtils.launchImagePicker() },
                    registerImageCallback = { onImageUploaded = it },
                    userRepo = userRepo,
                    onDone = { finish() }
                )
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    onChangePhotoClick: () -> Unit,
    registerImageCallback: ((String?) -> Unit) -> Unit,
    userRepo: UserRepoImp,
    onDone: () -> Unit
) {
    var user by remember { mutableStateOf<UserModel?>(null) }
    var loading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        registerImageCallback { url ->
            if (url != null) imageUrl = url
        }

        if (userId != null) {
            userRepo.getUserById(userId) { success, _, fetchedUser ->
                if (success && fetchedUser != null) {
                    user = fetchedUser
                    name = fetchedUser.name
                    email = fetchedUser.email
                    phone = fetchedUser.contact
                    address = fetchedUser.address
                    imageUrl = fetchedUser.imageUrl
                }
                loading = false
            }
        } else {
            loading = false
        }
    }

    Scaffold(containerColor = Color.White) { padding ->

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
                .padding(horizontal = 20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDone) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = "Back"
                    )
                }
                Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                TextButton(onClick = {
                    if (userId != null) {
                        val updatedUser = UserModel(
                            id = userId,
                            name = name,
                            email = email,
                            contact = phone,
                            address = address,
                            imageUrl = imageUrl
                        )
                        userRepo.editProfile(userId, updatedUser) { _, _ -> onDone() }
                    }
                }) {
                    Text("Save", fontWeight = FontWeight.Bold, color = Color(0xFF52649A))
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl.isEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_24),
                            contentDescription = "Profile picture",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(50.dp)
                        )
                    } else {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Change Photo",
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onChangePhotoClick() }
                )
            }

            Spacer(Modifier.height(20.dp))

            EditField(label = "Name", value = name, onValueChange = { name = it })
            EditField(label = "Email address", value = email, onValueChange = {}, enabled = false)
            EditField(label = "Phone number", value = phone, onValueChange = { phone = it })
            EditField(label = "Address", value = address, onValueChange = { address = it })
            EditField(label = "City", value = city, onValueChange = { city = it })

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF111827))
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6),
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF52649A),
                disabledBorderColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    SmartLibrarySystemTheme {
        Text("Preview requires Activity context for image picker")
    }
}