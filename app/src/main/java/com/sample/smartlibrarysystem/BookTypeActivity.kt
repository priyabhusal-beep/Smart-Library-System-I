package com.sample.smartlibrarysystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sample.smartlibrarysystem.model.BookModel
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme
import com.sample.smartlibrarysystem.viewmodel.BookViewModel

class BookTypeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartLibrarySystemTheme {
                BookScreen()
            }
        }
    }
}

@Composable
fun BookScreen() {
    val context = LocalContext.current
    val bookViewModel = remember { BookViewModel() }

    var selectedGenre by remember { mutableStateOf("All Genres") }
    var firebaseBooks by remember { mutableStateOf<List<BookModel>>(emptyList()) }

    val defaultBooks = remember {
        listOf(
            BookModel(
                title = "The Burning Earth",
                author = "Sunil Amrith",
                type = "Science",
                rating = 0.0,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821469/itStartWithUse_ayn8ti.jpg"
            ),
            BookModel(
                title = "Too good to be true",
                author = "Prajakta Kohli",
                type = "Novel",
                rating = 0.0,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782837582/TheStartOfEarth_pozqah.jpg"
            ),
            BookModel(
                title = "Power of Focus",
                author = "Daniel Reed",
                type = "Self Growth",
                rating = 4.5,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821517/lookingForJane_wrcn6c.jpg"
            ),
            BookModel(
                title = "Literal Truth",
                author = "Emma Brooks",
                type = "Novel",
                rating = 4.1,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821435/itsendwithus_ouo4xb.jpg"
            ),
            BookModel(
                title = "To Kill a Mockingbird",
                author = "Harper Lee",
                type = "Novel",
                rating = 4.6,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821509/literal_qwxc3k.jpg"
            ),
            BookModel(
                title = "Physics",
                author = "Stephen Golding",
                type = "Science",
                rating = 4.2,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821503/ladyTan_s_yugw65.jpg"
            ),
            BookModel(
                title = "C++ Programming",
                author = "Bjarne Stroustrup",
                type = "Programming",
                rating = 4.8,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821423/houseOf_i4b93r.jpg"
            ),
            BookModel(
                title = "Technology Today",
                author = "Mark Stone",
                type = "Technology",
                rating = 4.3,
                isAvailable = false,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782820506/novel_dcltqn.jpg"
            )
        )
    }

    LaunchedEffect(Unit) {
        bookViewModel.getAllBooks { success, _, fetchedBooks ->
            if (success) {
                firebaseBooks = fetchedBooks
            }
        }
    }

    val allBooks = defaultBooks + firebaseBooks

    val filteredBooks = allBooks.filter {
        selectedGenre == "All Genres" || it.type == selectedGenre
    }

    Scaffold(containerColor = Color(0xFFFAF7F8)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFAF7F8), Color(0xFFF3ECEF))
                    )
                )
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MyLibrary",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAA3E3E)
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("📚", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GenreDropdown(
                selectedGenre = selectedGenre,
                onGenreSelected = { selectedGenre = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Showing ${filteredBooks.size} results",
                fontSize = 15.sp,
                color = Color(0xFF6D6675)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBooks) { book ->
                    BookCard(
                        book = book,
                        onViewDetailsClick = {
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
                }
            }
        }
    }
}

@Composable
fun GenreDropdown(
    selectedGenre: String,
    onGenreSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val genres = listOf(
        "All Genres",
        "Science",
        "Novel",
        "Self Growth",
        "Programming",
        "Technology"
    )

    Box {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedGenre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.weight(1f))
                Text("⌄", fontSize = 18.sp)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        onGenreSelected(genre)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookModel,
    onViewDetailsClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF1EBED))
            ) {
                AsyncImage(
                    model = book.imageUrl.trim(),
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                    contentScale = ContentScale.Crop
                )

                if (book.isAvailable) {
                    Surface(
                        color = Color(0xFF22C55E),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Available",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(32.dp)
                        .clickable { isFavorite = !isFavorite }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isFavorite) "♥" else "♡",
                            color = Color(0xFFC65398),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Surface(
                    color = Color(0xFFFCE7F3),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = book.type,
                        color = Color(0xFFC65398),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = book.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = book.author,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "⭐ ${book.rating}",
                    fontSize = 12.sp,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onViewDetailsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC65398)
                    )
                ) {
                    Text(
                        text = "View Details",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookPreview() {
    SmartLibrarySystemTheme {
        BookCard(
            book = BookModel(
                title = "The Burning Earth",
                author = "Sunil Amrith",
                type = "Science",
                rating = 4.5,
                isAvailable = true,
                imageUrl = "https://res.cloudinary.com/dslkldwca/image/upload/v1782821469/itStartWithUse_ayn8ti.jpg"
            ),
            onViewDetailsClick = {}
        )
    }
}