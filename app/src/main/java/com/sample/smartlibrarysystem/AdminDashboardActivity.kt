package com.sample.smartlibrarysystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.sample.smartlibrarysystem.model.BookModel
import com.sample.smartlibrarysystem.model.UserModel
import com.sample.smartlibrarysystem.repo.ImageRepoImp
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme
import com.sample.smartlibrarysystem.viewmodel.BookViewModel
import com.sample.smartlibrarysystem.viewmodel.ImageViewModel
//import androidx.compose.ui.platform.LocalContext
//import com.sample.smartlibrarysystem.viewmodel.UserViewModel
class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartLibrarySystemTheme {
                AdminDashboardScreen()
            }
        }
    }
}

@Composable
fun AdminDashboardScreen() {

    var selectedTab by remember { mutableStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current
    Scaffold(
        containerColor = Color(0xFFF8F7FF),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("📚", fontSize = 20.sp) },
                    label = { Text("Books") }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("👥", fontSize = 20.sp) },
                    label = { Text("Users") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(
                            Intent(context, EditBooksActivity::class.java)
                        )
                    },
                    icon = { Text("✏️", fontSize = 20.sp) },
                    label = { Text("Edit") }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("👤", fontSize = 20.sp) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> AddBookScreen(Modifier.padding(padding))
            1 -> TotalUsersScreen(Modifier.padding(padding))
            2 -> AdminSectionScreen(Modifier.padding(padding))
           }

    }
}

@Composable
fun AddBookScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val bookViewModel = remember { BookViewModel() }
    val imageViewModel = remember { ImageViewModel(ImageRepoImp()) }

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Add Books",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAA3E3E)
        )

        Text(
            text = "Choose image from gallery. It will upload to Cloudinary.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(20.dp))

        AdminTextField(title, { title = it }, "Book Title")
        Spacer(Modifier.height(10.dp))

        AdminTextField(author, { author = it }, "Author")
        Spacer(Modifier.height(10.dp))

        CategoryDropdown(
            selectedCategory = type,
            onCategorySelected = { type = it }
        )
        Spacer(Modifier.height(10.dp))

        AdminTextField(rating, { rating = it }, "Rating")
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = summary,
            onValueChange = { summary = it },
            label = { Text("Book Summary") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                imagePicker.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Choose Book Image")
        }

        selectedImageUri?.let { uri ->
            Spacer(Modifier.height(12.dp))

            AsyncImage(
                model = uri,
                contentDescription = "Selected book image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = {
                if (
                    title.isBlank() ||
                    author.isBlank() ||
                    type.isBlank() ||
                    summary.isBlank() ||
                    selectedImageUri == null
                ) {
                    Toast.makeText(context, "Please fill all fields and choose image", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                uploading = true

                imageViewModel.uploadImage(context, selectedImageUri!!) { uploadSuccess, result ->
                    if (!uploadSuccess) {
                        uploading = false
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                        return@uploadImage
                    }

                    val uploadedUrl = result

                    val book = BookModel(
                        title = title.trim(),
                        author = author.trim(),
                        type = type.trim(),
                        rating = rating.toDoubleOrNull() ?: 0.0,
                        isAvailable = true,
                        imageUrl = uploadedUrl,
                        summary = summary.trim()
                    )

                    bookViewModel.addBook(book) { success, message ->
                        uploading = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        if (success) {
                            title = ""
                            author = ""
                            type = ""
                            rating = ""
                            summary = ""
                            selectedImageUri = null
                        }
                    }
                }
            },
            enabled = !uploading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA3E3E))
        ) {
            Text(
                text = if (uploading) "Uploading..." else "Add Book",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
fun TotalUsersScreen(modifier: Modifier = Modifier) {
//    val context = LocalContext.current
//    val userViewModel = remember { UserViewModel() }
    var users by remember { mutableStateOf<List<UserModel>>(emptyList()) }


    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<UserModel>()

                for (child in snapshot.children) {
                    if (child.key != "admin") {
                        val user = child.getValue(UserModel::class.java)
                        if (user != null) list.add(user)
                    }
                }

                users = list
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Registered Users",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAA3E3E)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Total Users: ${users.size}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = user.name.ifEmpty { "No Name" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(user.email, color = Color.Gray, fontSize = 13.sp)
                        Text(user.contact, color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSectionScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Admin Section",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAA3E3E)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "You are logged in as Admin.",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                context.startActivity(Intent(context, LoginScreen::class.java))
                (context as? ComponentActivity)?.finish()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun AdminTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Novel",
        "Adventure",
        "Science",
        "Self Growth",
        "Programming",
        "Technology"
    )

    Box {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category / Genre") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Text("⌄", modifier = Modifier.clickable { expanded = true })
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}