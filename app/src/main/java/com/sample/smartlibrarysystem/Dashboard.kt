package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sample.smartlibrarysystem.model.UserModel
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartLibrarySystemTheme {
                DashboardScreen()
            }
        }
    }
}

data class BookType(
    val name: String,
    val imageRes: Int
)

data class RecommendedBook(
    val title: String,
    val author: String,
    val imageUrl: String
)

@Composable
fun DashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var userName by remember { mutableStateOf("") }
    var userImageUrl by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId)
                .get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(UserModel::class.java)
                    userName = user?.name ?: ""
                    userImageUrl = user?.imageUrl ?: ""
                }
        }
    }

    Scaffold(
        topBar = { TopHeader(userName, userImageUrl) },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedTab,
                onItemSelected = { index ->
                    selectedTab = index

                    when (index) {
                        0 -> {
                            // Already on Dashboard
                        }

                        1 -> {
                            context.startActivity(
                                Intent(context, BookTypeActivity::class.java)
                            )
                        }

                        2 -> {
                            Toast.makeText(context, "History screen not created yet", Toast.LENGTH_SHORT).show()
                        }

                        3 -> {
                            context.startActivity(
                                Intent(context, ProfileScreen::class.java)
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F7FF)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar()

            Spacer(Modifier.height(16.dp))

            QuickActions(
                onTypeClick = { typeName ->
                    context.startActivity(
                        Intent(context, BookTypeActivity::class.java).apply {
                            putExtra("BOOK_TYPE", typeName)
                        }
                    )
                }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Recommended for You",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(10.dp))

            RecommendedBooks()

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Borrowed Books",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(10.dp))

            BorrowedBooks()
        }
    }
}

@Composable
fun TopHeader(userName: String, imageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl.ifEmpty { "https://res.cloudinary.com/dslkldwca/image/upload/v1/sample.jpg" },
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(10.dp))

            Column {
                Text(
                    text = "Good Morning,",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = "${userName.ifEmpty { "Username" }} 👋",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(R.drawable.baseline_notifications_24),
                contentDescription = "Notifications",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search books, authors, categories...",
                fontSize = 14.sp
            )
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun QuickActions(onTypeClick: (String) -> Unit = {}) {
    Text(
        text = "Types of Books",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color(0xFF111827)
    )

    Spacer(Modifier.height(10.dp))

    val bookTypes = listOf(
        BookType(name = "Novel", imageRes = R.drawable.novel),
        BookType(name = "Adventure", imageRes = R.drawable.adventure),
        BookType(name = "Self Growth", imageRes = R.drawable.selfgrowth),
        BookType(name = "Romantic", imageRes = R.drawable.romantic),
        BookType(name = "Science", imageRes = R.drawable.science)
    )

    LazyRow {
        items(bookTypes.size) { index ->
            val type = bookTypes[index]

            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(85.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEAF3FF))
                    .clickable {
                        onTypeClick(type.name)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = type.imageRes),
                    contentDescription = type.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = type.name,
                    fontSize = 11.sp,
                    color = Color(0xFF374151),
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendedBooks() {
    val books = listOf(
        RecommendedBook(
            title = "Book Title",
            author = "Author Name",
            imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1/book1.jpg"
        ),
        RecommendedBook(
            title = "Book Title",
            author = "Author Name",
            imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1/book2.jpg"
        ),
        RecommendedBook(
            title = "Book Title",
            author = "Author Name",
            imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1/book3.jpg"
        )
    )

    LazyRow {
        items(books.size) { index ->
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = books[index].imageUrl,
                    contentDescription = books[index].title,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = books[index].title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF111827)
                )

                Text(
                    text = books[index].author,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF52649A)
                    )
                ) {
                    Text("Borrow")
                }
            }
        }
    }
}

@Composable
fun BorrowedBooks() {
    Column {
        repeat(2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray, RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "George Orwell",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Text(
                        text = "1984",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "Active",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: Int
)

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.baseline_home_24),
        BottomNavItem("Search", R.drawable.baseline_search_24),
        BottomNavItem("History", R.drawable.baseline_history_24),
        BottomNavItem("Profile", R.drawable.baseline_person_24)

    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedIndex == index

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) Color(0xFFEAF3FF)
                        else Color.Transparent
                    )
                    .clickable {
                        onItemSelected(index)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = item.title,
                    tint = if (selected) Color(0xFF1A237E) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = item.title,
                    fontSize = 10.sp,
                    color = if (selected) Color(0xFF1A237E) else Color.Gray,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    SmartLibrarySystemTheme {
        DashboardScreen()
    }
}