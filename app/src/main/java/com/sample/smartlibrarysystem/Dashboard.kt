package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
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
import com.sample.smartlibrarysystem.model.RentedBookModel
import com.sample.smartlibrarysystem.model.UserModel
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    var rentedBooks by remember { mutableStateOf<List<RentedBookModel>>(emptyList()) }

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

            FirebaseDatabase.getInstance()
                .getReference("rented_books")
                .child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<RentedBookModel>()

                        for (child in snapshot.children) {
                            val book = child.getValue(RentedBookModel::class.java)

                            if (book != null && book.title.isNotBlank()) {
                                list.add(book)
                            }
                        }

                        rentedBooks = list.sortedByDescending { it.rentedAt }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // No action needed
                    }
                })
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
        containerColor = Color(0xFFFDF6F8),
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedTab,
                onItemSelected = { index ->
                    selectedTab = index

                    when (index) {
                        1 -> context.startActivity(Intent(context, BookTypeActivity::class.java))
                        3 -> context.startActivity(Intent(context, ProfileScreen::class.java))
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

            if (selectedTab == 2) {
                BorrowedBooksScreen(rentedBooks = rentedBooks)
            } else {
                TopHeader(
                    userName = userName,
                    imageUrl = userImageUrl
                )

                Spacer(Modifier.height(12.dp))

                SearchBar {
                    context.startActivity(Intent(context, BookTypeActivity::class.java))
                }

                Spacer(Modifier.height(18.dp))

                QuickActions { typeName ->
                    context.startActivity(
                        Intent(context, BookTypeActivity::class.java).apply {
                            putExtra("BOOK_TYPE", typeName)
                        }
                    )
                }

                Spacer(Modifier.height(22.dp))

                SectionTitle("Recommended for You")

                Spacer(Modifier.height(12.dp))

                RecommendedBooks(
                    books = books,
                    onBookClick = { book ->
                        openBookDetails(context, book)
                    }
                )

                Spacer(Modifier.height(24.dp))

                SectionTitle("Borrowed Books")

                Spacer(Modifier.height(12.dp))

                BorrowedBooksPreview(rentedBooks = rentedBooks)
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

fun openBookDetails(context: android.content.Context, book: BookModel) {
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

@Composable
fun TopHeader(userName: String, imageUrl: String) {
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
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hi, ${userName.ifEmpty { "Student" }}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1E3B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Welcome back to your library",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(R.drawable.baseline_notifications_24),
                contentDescription = "Notifications",
                tint = Color(0xFF7B1E3B)
            )
        }
    }
}

@Composable
fun SearchBar(onSearchClick: () -> Unit) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onSearchClick() },
        placeholder = {
            Text("Search books...", fontSize = 12.sp, color = Color.Gray)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_search_24),
                contentDescription = "Search",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFB45454),
            unfocusedBorderColor = Color(0xFFE3C5CC),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111827)
    )
}

@Composable
fun QuickActions(onTypeClick: (String) -> Unit) {
    SectionTitle("Types of Books")

    Spacer(Modifier.height(10.dp))

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
                    .padding(end = 10.dp)
                    .width(88.dp)
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
                        .height(82.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = type.name,
                    fontSize = 11.sp,
                    color = Color(0xFF374151),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 7.dp, horizontal = 5.dp)
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
        EmptyCard("No books available yet.")
        return
    }

    LazyRow {
        items(books.take(8)) { book ->
            BookCard(book = book, onBookClick = onBookClick)
        }
    }
}

@Composable
fun BorrowedBooksPreview(rentedBooks: List<RentedBookModel>) {
    if (rentedBooks.isEmpty()) {
        EmptyCard("No borrowed books yet.")
        return
    }

    LazyRow {
        items(rentedBooks.take(5)) { book ->
            RentedBookCard(book = book)
        }
    }
}

@Composable
fun BorrowedBooksScreen(rentedBooks: List<RentedBookModel>) {
    Text(
        text = "Borrowed Books",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF7B1E3B)
    )

    Spacer(Modifier.height(4.dp))

    Text(
        text = "Books you have rented",
        fontSize = 13.sp,
        color = Color.Gray
    )

    Spacer(Modifier.height(18.dp))

    if (rentedBooks.isEmpty()) {
        EmptyCard("No rented books found.")
    } else {
        rentedBooks.forEach { book ->
            RentedBookRow(book = book)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun RentedBookCard(book: RentedBookModel) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(150.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = book.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF111827),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = book.author,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RentedBookRow(book: RentedBookModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(width = 72.dp, height = 92.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = book.author,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Currently Borrowed",
                    fontSize = 11.sp,
                    color = Color(0xFFB45454),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookModel,
    onBookClick: (BookModel) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(150.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = book.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF111827),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = book.author,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onBookClick(book) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45454))
            ) {
                Text("View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmptyCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(18.dp),
            color = Color.Gray,
            fontSize = 14.sp
        )
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
        BottomNavItem("Borrowed", R.drawable.baseline_library_books_24),
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
                        modifier = Modifier.size(21.dp)
                    )
                },
                label = {
                    Text(text = item.title, fontSize = 9.sp)
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