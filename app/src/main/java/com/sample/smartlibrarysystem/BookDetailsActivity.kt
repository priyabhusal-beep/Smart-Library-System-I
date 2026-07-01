package com.sample.smartlibrarysystem

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme

class BookDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val title = intent.getStringExtra("title") ?: "Book Title"
        val author = intent.getStringExtra("author") ?: "Author Name"
        val type = intent.getStringExtra("type") ?: "Novel"
        val rating = intent.getDoubleExtra("rating", 0.0)
        val available = intent.getBooleanExtra("available", true)
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val summary = intent.getStringExtra("summary") ?: ""

        setContent {
            SmartLibrarySystemTheme {
                BookDetailsScreen(
                    title = title,
                    author = author,
                    type = type,
                    rating = rating,
                    available = available,
                    imageUrl = imageUrl,
                    summary = summary
                )
            }
        }
    }
}

@Composable
fun BookDetailsScreen(
    title: String,
    author: String,
    type: String,
    rating: Double,
    available: Boolean,
    imageUrl: String,
    summary: String
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        containerColor = Color(0xFFF8F2F4),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F2F4))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Rent Book: $title", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB44444))
                ) {
                    Text("📖 Rent Book", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Added to Wishlist: $title", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFB44444)
                    )
                ) {
                    Text("♡ Wishlist", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                    Text("←", fontSize = 26.sp, color = Color(0xFFAA3E3E))
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "📚 Book Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAA3E3E)
                )

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }

            Text(
                text = "Preview this book before borrowing",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            AsyncImage(
                model = imageUrl.trim(),
                contentDescription = title,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(170.dp)
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                ChipText(type, Color(0xFFFCE7F3), Color(0xFFC65398))
                ChipText("⭐ $rating", Color(0xFFFCE7F3), Color(0xFFC65398))
                ChipText(
                    if (available) "Available" else "Not Available",
                    if (available) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    if (available) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B2F2F)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = author,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(14.dp))

            Divider(color = Color(0xFFB7A6A6))

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Summary",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAA3E3E)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = summary.ifEmpty {
                    "$title is a useful and interesting book for readers. It helps users explore knowledge, ideas, and learning in a simple way."
                },
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
fun ChipText(text: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(
            text = text,
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}