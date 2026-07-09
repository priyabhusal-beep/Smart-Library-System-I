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
import com.sample.smartlibrarysystem.model.RentedBookModel

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
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("📚", fontSize = 18.sp) },
                    label = { Text("Books", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("👥", fontSize = 18.sp) },
                    label = { Text("Users", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, EditBooksActivity::class.java))
                    },
                    icon = { Text("✏️", fontSize = 18.sp) },
                    label = { Text("Edit", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("📖", fontSize = 18.sp) },
                    label = { Text("Rents", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Text("👤", fontSize = 18.sp) },
                    label = { Text("Profile", fontSize = 11.sp) }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> AddBookScreen(Modifier.padding(padding))
            1 -> TotalUsersScreen(Modifier.padding(padding))
            2 -> AdminRentedBooksScreen(Modifier.padding(padding))
            3 -> AdminSectionScreen(Modifier.padding(padding))
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val userViewModel = remember { com.sample.smartlibrarysystem.viewmodel.UserViewModel() }

    var users by remember { mutableStateOf<List<UserModel>>(emptyList()) }

    fun loadUsers() {
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

    LaunchedEffect(Unit) {
        loadUsers()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF6F8))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Registered Users",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7B1E3B)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Manage all student accounts",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4EC))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥",
                    fontSize = 28.sp
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Total Users",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = users.size.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1E3B)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (users.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "No registered users found.",
                    modifier = Modifier.padding(18.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(users) { user ->
                    var showDialog by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = Color(0xFFFFE4EC),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.firstOrNull()?.uppercase() ?: "U",
                                    color = Color(0xFF7B1E3B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.name.ifEmpty { "No Name" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111827),
                                    maxLines = 1
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = user.email,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = user.contact.ifEmpty { "No phone number" },
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = { showDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFDC2626)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Delete",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text(
                                    text = "Delete User",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text("Are you sure you want to delete ${user.name.ifEmpty { "this user" }}?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        userViewModel.deleteUser(user.id) { success, message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            if (success) {
                                                loadUsers()
                                            }
                                        }

                                        showDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDC2626)
                                    )
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = { showDialog = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
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
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                    contentDescription = "Dropdown",
                    tint = Color(0xFFAA3E3E),
                    modifier = Modifier.clickable { expanded = true }
                )
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

data class AdminRentItem(
    val rentId: String = "",
    val userId: String = "",
    val userName: String = "Unknown User",
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val paymentMethod: String = "N/A",
    val rentFee: Int = 0,
    val status: String = "Pending",
    val rentedAt: Long = 0L
)
@Composable
fun AdminRentedBooksScreen(modifier: Modifier = Modifier) {
    var rentedBooks by remember { mutableStateOf<List<AdminRentItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance()

        database.getReference("users").get()
            .addOnSuccessListener { usersSnapshot ->

                val userNames = mutableMapOf<String, String>()

                for (userChild in usersSnapshot.children) {
                    val uid = userChild.key ?: ""
                    val name = userChild.child("name").getValue(String::class.java) ?: "Unknown User"
                    userNames[uid] = name
                }

                database.getReference("rented_books").get()
                    .addOnSuccessListener { rentSnapshot ->

                        val list = mutableListOf<AdminRentItem>()

                        for (userNode in rentSnapshot.children) {
                            val userId = userNode.key ?: ""

                            for (rentNode in userNode.children) {
                                val title = rentNode.child("title").getValue(String::class.java) ?: ""

                                if (title.isNotBlank()) {
                                    val item = AdminRentItem(
                                        rentId = rentNode.child("rentId").getValue(String::class.java)
                                            ?: rentNode.key
                                            ?: "",
                                        userId = userId,
                                        userName = userNames[userId] ?: "Unknown User",
                                        title = title,
                                        author = rentNode.child("author").getValue(String::class.java) ?: "Unknown Author",
                                        imageUrl = rentNode.child("imageUrl").getValue(String::class.java) ?: "",
                                        paymentMethod = rentNode.child("paymentMethod").getValue(String::class.java) ?: "N/A",
                                        rentFee = rentNode.child("rentFee").getValue(Int::class.java) ?: 0,
                                        status = rentNode.child("status").getValue(String::class.java) ?: "Pending",
                                        rentedAt = rentNode.child("rentedAt").getValue(Long::class.java) ?: 0L
                                    )

                                    list.add(item)
                                }
                            }
                        }

                        rentedBooks = list.sortedByDescending { it.rentedAt }
                    }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF6F8))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Rented Books",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7B1E3B)
        )

        Text(
            text = "View user rentals and payment status",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4EC))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📖", fontSize = 28.sp)

                Spacer(Modifier.width(12.dp))

                Column {
                    Text("Total Rented", fontSize = 13.sp, color = Color.Gray)

                    Text(
                        text = rentedBooks.size.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1E3B)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (rentedBooks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "No rented books found.",
                    modifier = Modifier.padding(18.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(rentedBooks) { rent ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = rent.imageUrl,
                                contentDescription = rent.title,
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(95.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFE5E7EB)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rent.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111827),
                                    maxLines = 1
                                )

                                Text(
                                    text = rent.author,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = "User: ${rent.userName}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7B1E3B),
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Payment: ${rent.paymentMethod}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151)
                                )

                                Text(
                                    text = "Fee: Rs. ${rent.rentFee}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151)
                                )

                                Text(
                                    text = "Status: ${rent.status}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF16A34A),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}