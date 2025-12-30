// File: org.dm.petsociety.ui.screens.common.ChatListScreen.kt (REVISI LENGKAP)

package org.dm.petsociety.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.dm.petsociety.model.Chat
import org.dm.petsociety.ui.screens.AppBottomBar
import org.dm.petsociety.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// Warna Tema
val ChatBg = Color(0xFF141927)
val ChatItemBg = Color(0xFF1F2937)
val AccentGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatViewModel,
    role: String // "Pet Owner", "Shelter", "Expert" - Untuk BottomBar dan label
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val chats by viewModel.chats.collectAsState()

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) viewModel.loadChats(currentUserId)
    }

    // Tentukan route bottom bar berdasarkan role
    val bottomBarRoute = when {
        role.contains("Shelter") -> "home_shelter"
        role.contains("Expert") -> "home_expert"
        else -> "home_owner_chat"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Masuk", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChatBg)
            )
        },
        bottomBar = {
            if (role.contains("Pet Owner")) {
                AppBottomBar(navController, bottomBarRoute)
            }
        },
        containerColor = ChatBg
    ) { padding ->
        if (chats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ChatBubbleOutline, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Belum ada pesan.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // TEXT SECONDARY DENGAN BATASAN ROLE (MUNCUL DI PALING ATAS LIST)
                    val headerText = when {
                        role.contains("Pet Owner") -> "Daftar Chat Anda dengan Owner dan Rescuer:"
                        role.contains("Shelter") -> "Daftar Chat Anda dengan Pet Owner:"
                        role.contains("Expert") -> "Daftar Chat Layanan Anda:"
                        else -> "Pesan Pribadi:"
                    }
                    Text(
                        headerText,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(chats) { chat ->
                    ChatListItem(chat, currentUserId, navController, role, onDelete = { viewModel.deleteChat(chat.chatId) })
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: Chat,
    currentUserId: String,
    navController: NavController,
    currentRole: String, // Role Pengguna Saat Ini
    onDelete: () -> Unit
) {
    val displayName = chat.companionName ?: "User"
    val companionRole = chat.groupType ?: "Pet Owner" // Menggunakan groupType untuk menyimpan Role lawan bicara

    // Logic untuk menentukan Secondary Text (Batasan Role)
    val secondaryText = when {
        // Sisi Pet Owner
        currentRole.contains("Pet Owner") && companionRole.contains("Shelter") -> "dari Shelter/Rescuer"
        currentRole.contains("Pet Owner") && companionRole.contains("Expert") -> "dari Expert/Bisnis"
        currentRole.contains("Pet Owner") -> "dari Pet Owner lain" // Default untuk Pet Owner

        // Sisi Shelter
        currentRole.contains("Shelter") -> "dari Pet Owner"

        // Sisi Expert
        currentRole.contains("Expert") -> "dari Pet Owner"

        else -> "Pesan Pribadi"
    }

    // Logic Delete Dialog (Sederhana)
    var showDelete by remember { mutableStateOf(false) }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Hapus Chat?") },
            confirmButton = { Button(onClick = { onDelete(); showDelete = false }) { Text("Hapus") } },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Batal") } }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChatItemBg, RoundedCornerShape(12.dp))
            .clickable {
                // Navigasi ke detail
                navController.navigate("chat_detail/${chat.chatId}/$displayName")
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(displayName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Konten
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(chat.timestamp.toDate()),
                    color = Color.Gray, fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = secondaryText, // Teks Sekunder: Label Role/Batasan
                color = AccentGreen, // Warna Aksen
                fontSize = 12.sp
            )
            Text(
                text = chat.lastMessage, // Isi Pesan Terakhir
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Delete Icon
        IconButton(onClick = { showDelete = true }) {
            Icon(Icons.Default.Delete, null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
        }
    }
}