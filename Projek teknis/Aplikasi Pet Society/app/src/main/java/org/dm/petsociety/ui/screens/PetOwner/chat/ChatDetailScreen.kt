package org.dm.petsociety.ui.screens.petowner.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import org.dm.petsociety.model.ChatMessage
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// Warna
val ChatBg = Color(0xFF141927)
val BubbleSelf = Color(0xFF4CAF50)
val BubbleOther = Color(0xFF2E3B59)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    navController: NavController,
    chatId: String,
    companionName: String,
    authViewModel: AuthViewModel // Parameter dummy agar kompatibel dengan NavHost, bisa tidak dipakai
) {
    val chatViewModel: ChatViewModel = viewModel()
    val messages by chatViewModel.messages.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var text by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        chatViewModel.loadMessages(chatId)
    }

    Scaffold(
        containerColor = ChatBg,
        topBar = {
            TopAppBar(
                title = { Text(companionName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChatBg)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F2937)) // Abu gelap
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Ketik pesan...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF141927),
                        unfocusedContainerColor = Color(0xFF141927),
                        focusedBorderColor = BubbleSelf,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (text.isNotBlank() && currentUser != null) {
                            val userName = currentUser.displayName ?: "User"
                            chatViewModel.sendMessage(chatId, text, currentUser.uid, userName)
                            text = ""
                        }
                    },
                    modifier = Modifier.background(BubbleSelf, androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            reverseLayout = true // Pesan terbaru di bawah (teknis lazycolumn reverse)
        ) {
            // Kita perlu membalik list messages karena reverseLayout=true memutar urutan tampilan
            items(messages.reversed()) { msg ->
                MessageBubble(msg, currentUser?.uid ?: "")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage, myUid: String) {
    val isMe = msg.userId == myUid
    val align = if (isMe) Alignment.End else Alignment.Start
    val color = if (isMe) BubbleSelf else BubbleOther
    val shape = if (isMe) RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp) else RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Surface(color = color, shape = shape, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(msg.content, color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(msg.timestamp.toDate()),
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}