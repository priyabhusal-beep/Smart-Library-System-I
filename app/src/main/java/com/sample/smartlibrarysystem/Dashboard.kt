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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sample.smartlibrarysystem.model.BookModel
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

data class BottomNavItem(
    val title: String,
    val icon: Int
)

@Composable
fun DashboardScreen() {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    var userName by remember { mutableStateOf("") }
    var userImageUrl by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<BookModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(UserModel::class.java)
                    userName = user?.name ?: ""
                    userImageUrl = user?.imageUrl ?: ""
                }
        }

        FirebaseDatabase.getInstance()
            .getReference("books")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<BookModel>()

                for (child in snapshot.children) {
                    val book = child.getValue(BookModel::class.java)
                    if (book != null) list.add(book)
                }

                books = list
            }
    }

    Scaffold(
        containerColor = Color(0xFFF8F7FF),
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedTab,
                onItemSelected = { index ->
                    selectedTab = index

                    when (index) {
                        0 -> Unit

                        1 -> {
                            context.startActivity(
                                Intent(context, BookTypeActivity::class.java)
                            )
                        }

                        2 -> {
                            Toast.makeText(
                                context,
                                "History screen not created yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        3 -> {
                            context.startActivity(
                                Intent(context, ProfileScreen::class.java)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            TopHeader(
                userName = userName,
                imageUrl = userImageUrl
            )

            Spacer(Modifier.height(18.dp))

            SearchBar(
                onSearchClick = {
                    context.startActivity(
                        Intent(context, BookTypeActivity::class.java)
                    )
                }
            )

            Spacer(Modifier.height(20.dp))

            QuickActions(
                onTypeClick = { typeName ->
                    context.startActivity(
                        Intent(context, BookTypeActivity::class.java).apply {
                            putExtra("BOOK_TYPE", typeName)
                        }
                    )
                }
            )

            Spacer(Modifier.height(24.dp))

            SectionTitle("Recommended for You")

            Spacer(Modifier.height(12.dp))

            RecommendedBooks(
                books = books,
                onBookClick = { book ->
                    context.startActivity(
                        Intent(context, BookDetailsActivity::class.java).apply {
                            putExtra("title", book.title)
                            putExtra("author", book.author)
                            putExtra("type", book.type)
                            putExtra("rating", book.rating)
                            putExtra("available", book.isAvailable)
                            putExtra("imageUrl", book.imageUrl)
                            putExtra("summary", book.summary)
                        }
                    )
                }
            )

            Spacer(Modifier.height(24.dp))

            SectionTitle("Borrowed Books")

            Spacer(Modifier.height(12.dp))

            BorrowedBooks()

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun TopHeader(
    userName: String,
    imageUrl: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl.ifEmpty {
                "https://res.cloudinary.com/dslkldwca/image/upload/v1/sample.jpg"
            },
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome Back 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Text(
                text = userName.ifEmpty { "Student" },
                fontSize = 15.sp,
                color = Color.Gray
            )
        }

        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(R.drawable.baseline_notifications_24),
                contentDescription = "Notifications",
                tint = Color(0xFF111827)
            )
        }
    }
}

@Composable
fun SearchBar(
    onSearchClick: () -> Unit
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchClick() },
        placeholder = {
            Text(
                text = "Search books, authors, categories...",
                fontSize = 14.sp,
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_search_24),
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFB45454),
            unfocusedBorderColor = Color(0xFFD8C5C5),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111827)
    )
}

@Composable
fun QuickActions(
    onTypeClick: (String) -> Unit
) {
    SectionTitle("Types of Books")

    Spacer(Modifier.height(12.dp))

    val bookTypes = listOf(
        BookType("Novel", R.drawable.novel),
        BookType("Adventure", R.drawable.adventure),
        BookType("Self Growth", R.drawable.selfgrowth),
        BookType("Romantic", R.drawable.romantic),
        BookType("Science", R.drawable.science)
    )

    LazyRow {
        items(bookTypes) { type ->
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { onTypeClick(type.name) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = type.imageRes),
                    contentDescription = type.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = type.name,
                    fontSize = 12.sp,
                    color = Color(0xFF374151),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendedBooks(
    books: List<BookModel>,
    onBookClick: (BookModel) -> Unit
) {
    if (books.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = "No books available yet.",
                modifier = Modifier.padding(18.dp),
                color = Color.Gray
            )
        }
        return
    }

    LazyRow {
        items(books.take(8)) { book ->
            Card(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .width(170.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = book.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE5E7EB)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = book.author,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { onBookClick(book) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB45454)
                        )
                    ) {
                        Text(
                            text = "View",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BorrowedBooks() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "No borrowed books yet.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

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

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedIndex == index

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFB45454),
                    selectedTextColor = Color(0xFFB45454),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFFFE4E6)
                )
            )
        }
    }
}