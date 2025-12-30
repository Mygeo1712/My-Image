package org.dm.petsociety.ui.screens.petowner.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.dm.petsociety.model.Chat
import org.dm.petsociety.ui.screens.AppBottomBar
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.screens.SectionHeader
import org.dm.petsociety.ui.screens.formatTimestamp
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.ChatViewModel

// Menggunakan OptIn untuk fitur eksperimental: CombinedClickable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PetOwnerChatScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()
    val currentUserId = authState.userId ?: ""
    val chats by chatViewModel.chats.collectAsState()

    // State untuk manajemen dialog konfirmasi hapus
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedChatToDelete by remember { mutableStateOf<Chat?>(null) }

    // Memuat data chat saat screen aktif
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            chatViewModel.loadChats(currentUserId)
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Chat", navController = navController, showProfile = true) },
        containerColor = PrimaryDarkNavy,
        bottomBar = { AppBottomBar(navController = navController, currentRoute = "home_owner_chat") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryDarkNavy),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { SectionHeader("Group Chat") }

            // Group Chat Placeholder
            item {
                GroupChatPlaceholder(navController, "Pecinta Anjing")
                GroupChatPlaceholder(navController, "Pecinta Kucing")
                GroupChatPlaceholder(navController, "Pecinta Burung")
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { SectionHeader("Chat Pribadi") }

            if (chats.isEmpty()) {
                item {
                    Text(
                        "Mulai percakapan dengan pemilik hewan lain!",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            } else {
                // Filter hanya chat pribadi dan tampilkan list
                items(chats.filter { !it.chatIsGroup }, key = { it.chatId }) { chat ->
                    ChatListItem(
                        chat = chat,
                        currentUserId = currentUserId,
                        // FIX: Melewatkan companionName untuk navigasi Chat Detail
                        onChatClick = { companionName ->
                            navController.navigate("chat_detail/${chat.chatId}/${chat.companionName}")
                        },
                        // BARU: Menangani klik tahan
                        onLongClick = { selectedChat ->
                            selectedChatToDelete = selectedChat
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus Chat (Muncul saat Long Press)
    if (showDeleteDialog && selectedChatToDelete != null) {
        val chatToDelete = selectedChatToDelete!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedChatToDelete = null },
            title = { Text("Hapus Chat", color = PrimaryDarkNavy) },
            text = { Text("Apakah Anda yakin ingin menghapus riwayat chat dengan ${chatToDelete.companionName}?", color = PrimaryDarkNavy) },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    onClick = {
                        chatViewModel.deleteChat(chatToDelete.chatId) // Panggil ViewModel untuk menghapus
                        showDeleteDialog = false
                        selectedChatToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false; selectedChatToDelete = null }) {
                    Text("Batal", color = PrimaryDarkNavy)
                }
            }
        )
    }
}

@Composable
fun GroupChatPlaceholder(navController: NavController, groupName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { navController.navigate("chat_detail/GROUP_${groupName.replace(" ", "_")}/${groupName}") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B59)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Group, contentDescription = "Group", tint = Color.LightGray, modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.DarkGray).padding(8.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(groupName, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Lihat Anggota...", fontSize = 12.sp, color = AccentTeal)
            }
        }
    }
}


@Composable
fun ChatListItem(
    chat: Chat,
    currentUserId: String,
    onChatClick: (String) -> Unit,
    onLongClick: (Chat) -> Unit
) {
    // FIX LOGIKA: Dapatkan nama lawan bicara (yang seharusnya sudah di-fetch oleh ViewModel)
    val displayName = if (chat.chatIsGroup) {
        chat.groupName ?: "Group Chat"
    } else {
        // Menggunakan companionName yang sudah dikalkulasi oleh ChatViewModel (Nama Roi)
        chat.companionName ?: chat.participants.filter { it != currentUserId }.firstOrNull() ?: "Pengguna Lain"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .combinedClickable(
                onClick = { onChatClick(displayName) },
                onLongClick = { onLongClick(chat) }
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B59)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (chat.chatIsGroup) Icons.Filled.Group else Icons.Filled.Person,
                    contentDescription = "Chat",
                    tint = AccentTeal,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF33425F)).padding(8.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(displayName, fontWeight = FontWeight.Bold, color = Color.White) // FIX: Tampilkan nama lawan bicara
                    Text(
                        chat.lastMessage,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                }
            }

            Text(
                formatTimestamp(chat.timestamp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}