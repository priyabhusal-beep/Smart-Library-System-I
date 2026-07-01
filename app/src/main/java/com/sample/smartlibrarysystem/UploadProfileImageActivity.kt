package com.sample.smartlibrarysystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sample.smartlibrarysystem.repo.ImageRepoImp
import com.sample.smartlibrarysystem.utils.ImageUtils
import com.sample.smartlibrarysystem.viewmodel.ImageViewModel

class UploadProfileImageActivity : ComponentActivity() {
    private val imageViewModel = ImageViewModel(ImageRepoImp())
    private lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var imageUrlState: (String?) -> Unit = {}

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            if (uri != null) {
                imageViewModel.uploadImage(this, uri) { success, result ->
                    if (success) {
                        val url = result
                        imageUrlState(url)

                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(userId)
                                .child("imageUrl")
                                .setValue(url)
                        }
                    }
                }
            }
        }

        setContent {
            var imageUrl by remember { mutableStateOf<String?>(null) }
            imageUrlState = { imageUrl = it }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = imageUrl ?: "https://res.cloudinary.com/dslkldwca/image/upload/v1/sample.jpg",
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = { imageUtils.launchImagePicker() }) {
                    Text("Choose & Upload Image")
                }
            }
        }
    }
}