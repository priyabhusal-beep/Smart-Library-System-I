package com.sample.smartlibrarysystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/* ---------------- MAIN SCREEN ---------------- */
@Composable
fun DashboardScreen() {

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopHeader() },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            SearchBar()
            Spacer(Modifier.height(16.dp))

            QuickActions()
            Spacer(Modifier.height(20.dp))

            Text("Recommended for You", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            RecommendedBooks()

            Spacer(Modifier.height(20.dp))

            Text("Borrowed Books", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            BorrowedBooks()
        }
    }
}

/* ---------------- TOP HEADER ---------------- */
@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text("Good Morning,", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Username 👋", fontSize = 16.sp, color = Color.Gray)
        }

        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(R.drawable.baseline_notifications_24),
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

/* ---------------- SEARCH BAR ---------------- */
@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search books, authors, categories...") },
        shape = RoundedCornerShape(12.dp)
    )
}

/* ---------------- QUICK ACTIONS ---------------- */
@Composable
fun QuickActions() {

    Text("Types of Books", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(Modifier.height(10.dp))

    val actions = listOf("Novel", "Adventure", "Self Growth", "Romantic")

    LazyRow {
        items(actions.size) { index ->
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF3FF))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                )

                Spacer(Modifier.height(8.dp))

                Text(actions[index], fontSize = 12.sp)
            }
        }
    }
}

/* ---------------- RECOMMENDED BOOKS ---------------- */
@Composable
fun RecommendedBooks() {
    LazyRow {
        items(3) {
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(10.dp)
            ) {

                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray, RoundedCornerShape(10.dp))
                )

                Spacer(Modifier.height(8.dp))

                Text("Book Title", fontWeight = FontWeight.Bold)
                Text("Author Name", fontSize = 12.sp, color = Color.Gray)

                Spacer(Modifier.height(8.dp))

                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Borrow")
                }
            }
        }
    }
}

/* ---------------- BORROWED BOOKS ---------------- */
@Composable
fun BorrowedBooks() {
    Column {
        repeat(2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
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
                    Text("George Orwell", fontWeight = FontWeight.Bold)
                    Text("1984", fontSize = 12.sp, color = Color.Gray)
                }

                Text(
                    "Active",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ---------------- BOTTOM NAVIGATION ---------------- */

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
                    .padding(6.dp)
                    .background(if (selected) Color(0x22000000) else Color.Transparent)
                    .padding(6.dp)
                    .clickable() { onItemSelected(index) }
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

/* ---------------- PREVIEW ---------------- */
@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    SmartLibrarySystemTheme {
        DashboardScreen()
    }
}