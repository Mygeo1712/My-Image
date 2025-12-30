package org.dm.petsociety.ui.screens.shelter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Ini adalah Chat Screen KHUSUS Shelter
// Tampilannya sama persis, tapi TIDAK ADA BottomBar di dalamnya.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterChatScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Masuk") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
        // Perhatikan: Kita TIDAK menambahkan bottomBar di sini.
        // BottomBar akan di-handle oleh ShelterHomeScreen.
    ) { padding ->
        ChatListContent(navController = navController, modifier = Modifier.padding(padding))
    }
}

@Composable
fun ChatListContent(navController: NavController, modifier: Modifier = Modifier) {
    // Data Dummy Chat (Bisa diganti dengan data Firebase nanti)
    val chatList = listOf(
        ChatPreview("Budi (Pet Owner)", "Data Dummy Chat (Bisa diganti dengan data Firebase nanti)", "10:30"),
        ChatPreview("Siti (Adopter)", "Saya sudah di lokasi shelter.", "Kemarin"),
        ChatPreview("Dr. Hewan", "Jadwal vaksin besok ya.", "Senin")
    )

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(chatList) { chat ->
            ChatItem(chat) {
                // Navigasi ke Detail Chat
                // Pastikan rute "chat_detail" sudah ada di MainActivity
                // Kita gunakan ID dummy "123" untuk contoh
                navController.navigate("chat_detail/123/${chat.name}")
            }
        }
    }
}

data class ChatPreview(val name: String, val message: String, val time: String)

@Composable
fun ChatItem(chat: ChatPreview, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(50.dp).clip(CircleShape),
            color = Color(0xFFE0F7FA) // Warna sedikit beda untuk Shelter
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Color(0xFF006064))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = chat.message, color = Color.Gray, maxLines = 1)
        }

        Text(text = chat.time, fontSize = 12.sp, color = Color.Gray)
    }
}