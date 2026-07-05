package com.sample.smartlibrarysystem

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.sample.smartlibrarysystem.model.RentedBookModel
import com.sample.smartlibrarysystem.ui.theme.SmartLibrarySystemTheme

class PaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val title = intent.getStringExtra("title") ?: "Book"
        val author = intent.getStringExtra("author") ?: "Author"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        setContent {
            SmartLibrarySystemTheme {
                PaymentScreen(title, author, imageUrl)
            }
        }
    }
}

@Composable
fun PaymentScreen(title: String, author: String, imageUrl: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var paymentMethod by remember { mutableStateOf("Cash") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color(0xFFF8F2F4)) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TextButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                Text("← Back", color = Color(0xFFAA3E3E))
            }

            Text(
                text = "Payment Process",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAA3E3E)
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier
                            .width(80.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE5E7EB)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.width(14.dp))

                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(author, color = Color.Gray, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Rent Fee: Rs. 50", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Choose Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(Modifier.height(12.dp))

            PaymentOption("Cash", paymentMethod) { paymentMethod = it }
            PaymentOption("eSewa", paymentMethod) { paymentMethod = it }
            PaymentOption("Khalti", paymentMethod) { paymentMethod = it }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    loading = true

                    val rentRef = FirebaseDatabase.getInstance().getReference("rented_books")
                    val rentId = rentRef.push().key ?: ""

                    val rentedBook = RentedBookModel(
                        id = rentId,
                        title = title,
                        author = author,
                        imageUrl = imageUrl,
                        paymentMethod = paymentMethod,
                        rentFee = 50,
                        status = "Rented"
                    )

                    rentRef.child(rentId).setValue(rentedBook)
                        .addOnSuccessListener {
                            loading = false
                            Toast.makeText(context, "Book rented successfully", Toast.LENGTH_SHORT).show()
                            (context as? ComponentActivity)?.finish()
                        }
                        .addOnFailureListener {
                            loading = false
                            Toast.makeText(context, it.message ?: "Failed", Toast.LENGTH_SHORT).show()
                        }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB44444))
            ) {
                Text(
                    if (loading) "Processing..." else "Confirm Payment",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PaymentOption(
    name: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected == name,
                onClick = { onSelect(name) }
            )

            Spacer(Modifier.width(8.dp))

            Text(name, fontWeight = FontWeight.Bold)
        }
    }
}