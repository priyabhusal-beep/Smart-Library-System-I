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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sample.smartlibrarysystem.model.BookModel
import com.sample.smartlibrarysystem.repo.ImageRepoImp
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme
import com.sample.smartlibrarysystem.viewmodel.BookViewModel
import com.sample.smartlibrarysystem.viewmodel.ImageViewModel

class EditBooksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartLibrarySystemTheme {
                EditBooksScreen()
            }
        }
    }
}

@Composable
fun EditBooksScreen() {

    val context = LocalContext.current
    val bookViewModel = remember { BookViewModel() }

    var books by remember { mutableStateOf<List<BookModel>>(emptyList()) }
    var selectedBook by remember { mutableStateOf<BookModel?>(null) }

    fun loadBooks() {
        bookViewModel.getAllBooks { success, _, list ->
            if (success) books = list
        }
    }

    LaunchedEffect(Unit) {
        loadBooks()
    }

    Scaffold(

        containerColor = Color(0xFFF8F7FF),

        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFAA3E3E),
                    selectedTextColor = Color(0xFFAA3E3E),
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280),
                    indicatorColor = Color.Transparent
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(
                            Intent(context, AdminDashboardActivity::class.java).putExtra("tab", 0)
                        )
                        (context as? ComponentActivity)?.finish()
                    },
                    icon = { Text("📚", fontSize = 16.sp) },
                    label = { Text("Books", fontSize = 10.sp) },
                    colors = navColors
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(
                            Intent(context, AdminDashboardActivity::class.java).putExtra("tab", 1)
                        )
                        (context as? ComponentActivity)?.finish()
                    },
                    icon = { Text("👥", fontSize = 16.sp) },
                    label = { Text("Users", fontSize = 10.sp) },
                    colors = navColors
                )

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("✏️", fontSize = 16.sp) },
                    label = { Text("Edit", fontSize = 10.sp) },
                    colors = navColors
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(
                            Intent(context, AdminDashboardActivity::class.java).putExtra("tab", 2)
                        )
                        (context as? ComponentActivity)?.finish()
                    },
                    icon = { Text("📖", fontSize = 16.sp) },
                    label = { Text("Rents", fontSize = 10.sp) },
                    colors = navColors
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(
                            Intent(context, AdminDashboardActivity::class.java).putExtra("tab", 3)
                        )
                        (context as? ComponentActivity)?.finish()
                    },
                    icon = { Text("👤", fontSize = 16.sp) },
                    label = { Text("Profile", fontSize = 10.sp) },
                    colors = navColors
                )
            }
        }

    ) { innerPadding ->

        if (selectedBook == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Existing Books",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAA3E3E)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Total Books : ${books.size}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(books) { book ->

                        BookListCard(

                            book = book,

                            onEdit = {
                                selectedBook = book
                            },

                            onDelete = {

                                bookViewModel.deleteBook(book.id) { success, message ->

                                    Toast.makeText(
                                        context,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if (success)
                                        loadBooks()

                                }

                            }

                        )

                    }

                    item {
                        Spacer(Modifier.height(80.dp))
                    }

                }

            }

        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                EditBookForm(

                    book = selectedBook!!,

                    onBack = {
                        selectedBook = null
                    },

                    onUpdated = {
                        selectedBook = null
                        loadBooks()
                    }

                )

            }

        }

    }

}

@Composable
fun BookListCard(
    book: BookModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(width = 70.dp, height = 95.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(book.author, color = Color.Gray, fontSize = 12.sp)
                Text(book.type, color = Color(0xFFC65398), fontSize = 12.sp)
                Text("⭐ ${book.rating}", fontSize = 12.sp)
            }

            Column {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA3E3E))
                ) {
                    Text("Edit")
                }

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun EditBookForm(
    book: BookModel,
    onBack: () -> Unit,
    onUpdated: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val bookViewModel = remember { BookViewModel() }
    val imageViewModel = remember { ImageViewModel(ImageRepoImp()) }

    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author) }
    var type by remember { mutableStateOf(book.type) }
    var rating by remember { mutableStateOf(book.rating.toString()) }
    var summary by remember { mutableStateOf(book.summary) }
    var imageUrl by remember { mutableStateOf(book.imageUrl) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7FF))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text("← Back")
            }

            Text(
                text = "Edit Book",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAA3E3E)
            )
        }

        item { AdminTextField(title, { title = it }, "Title") }
        item { AdminTextField(author, { author = it }, "Author") }
        item { CategoryDropdown(type, { type = it }) }
        item { AdminTextField(rating, { rating = it }, "Rating") }

        item {
            OutlinedTextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text("Summary") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            AsyncImage(
                model = selectedImageUri ?: imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )
        }

        item {
            OutlinedButton(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Book Image")
            }
        }

        item {
            Button(
                onClick = {
                    uploading = true

                    fun saveBook(finalImageUrl: String) {
                        val updatedBook = book.copy(
                            title = title.trim(),
                            author = author.trim(),
                            type = type.trim(),
                            rating = rating.toDoubleOrNull() ?: 0.0,
                            summary = summary.trim(),
                            imageUrl = finalImageUrl
                        )

                        bookViewModel.updateBook(updatedBook) { success, message ->
                            uploading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) onUpdated()
                        }
                    }

                    if (selectedImageUri != null) {
                        imageViewModel.uploadImage(context, selectedImageUri!!) { success, result ->
                            if (success) saveBook(result)
                            else {
                                uploading = false
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        saveBook(imageUrl)
                    }
                },
                enabled = !uploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA3E3E))
            ) {
                Text(if (uploading) "Updating..." else "Update Book")
            }
        }

        item { Spacer(Modifier.height(50.dp)) }
    }
}