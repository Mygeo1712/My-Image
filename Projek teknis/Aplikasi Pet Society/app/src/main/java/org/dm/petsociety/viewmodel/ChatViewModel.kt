// File: org.dm.petsociety.viewmodel.ChatViewModel.kt (REVISI LENGKAP - TAMBAH ROLE)

package org.dm.petsociety.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.dm.petsociety.model.Chat
import org.dm.petsociety.model.ChatMessage

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Data Class untuk cache (dipakai internal)
    data class UserInfo(val name: String, val role: String)

    // 1. LOAD LIST CHAT (INBOX)
    fun loadChats(currentUserId: String) {
        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ChatVM", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val chatList = snapshot.toObjects(Chat::class.java)
                            .sortedByDescending { it.timestamp }

                        // Perulangan untuk mengisi nama dan peran lawan bicara
                        val chatsWithDetails = chatList.mapNotNull { chat ->
                            val otherId = chat.participants.find { it != currentUserId }
                            if (otherId != null) {
                                // Ambil nama dan peran lawan bicara
                                val userInfo = getCachedUserInfo(otherId)
                                chat.copy(
                                    companionName = userInfo.name,
                                    companionId = otherId,
                                    // Simpan peran lawan bicara di field yang belum terpakai (atau buat field baru)
                                    groupType = userInfo.role // Menggunakan groupType sebagai placeholder untuk role lawan bicara
                                )
                            } else {
                                chat
                            }
                        }
                        _chats.value = chatsWithDetails
                    }
                }
            }
    }

    // Fungsi helper untuk mengambil nama dan peran user dari Firestore
    private suspend fun getCachedUserInfo(userId: String): UserInfo {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val doc = db.collection("users").document(userId).get().await()
                UserInfo(
                    name = doc.getString("name") ?: "User",
                    role = doc.getString("role") ?: "Pet Owner"
                )
            } catch (e: Exception) {
                UserInfo(name = "User", role = "Pet Owner")
            }
        }
    }


    // 2. LOAD ISI PESAN (DETAIL CHAT) (Tetap sama)
    fun loadMessages(chatId: String) {
        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _messages.value = snapshot.toObjects(ChatMessage::class.java)
                }
            }
    }

    // 3. KIRIM PESAN (Tetap sama)
    fun sendMessage(chatId: String, content: String, currentUserId: String, currentUserName: String) {
        if (content.isBlank()) return

        val newMessage = ChatMessage(
            messageId = "",
            chatId = chatId,
            userId = currentUserId,
            username = currentUserName,
            content = content,
            timestamp = Timestamp.now()
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Simpan pesan ke sub-collection
                db.collection("chats").document(chatId).collection("messages").add(newMessage)

                // Update data terakhir di dokumen induk Chat (untuk list inbox)
                val updates = mapOf(
                    "lastMessage" to content,
                    "timestamp" to Timestamp.now()
                )
                db.collection("chats").document(chatId).update(updates)
            } catch (e: Exception) {
                Log.e("ChatVM", "Gagal kirim: ${e.message}")
            }
        }
    }

    // 4. MEMULAI CHAT (Universal Logic - Anti Duplikat) (Tetap sama)
    fun startChat(
        currentUid: String,
        targetUid: String,
        currentName: String,
        targetName: String,
        navController: NavController
    ) {
        if (currentUid == targetUid) return // Tidak bisa chat diri sendiri

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val chatId = if (currentUid < targetUid) "${currentUid}_${targetUid}" else "${targetUid}_${currentUid}"

                val chatRef = db.collection("chats").document(chatId)
                val snapshot = chatRef.get().await()

                if (!snapshot.exists()) {
                    val chatData = mapOf(
                        "chatId" to chatId,
                        "participants" to listOf(currentUid, targetUid).sorted(),
                        "lastMessage" to "Mulai percakapan...",
                        "timestamp" to Timestamp.now(),
                        "user_${currentUid}_name" to currentName,
                        "user_${targetUid}_name" to targetName
                    )
                    chatRef.set(chatData, SetOptions.merge()).await()
                }

                withContext(Dispatchers.Main) {
                    navController.navigate("chat_detail/$chatId/$targetName")
                }

            } catch (e: Exception) {
                Log.e("ChatVM", "Error starting chat: ${e.message}")
            }
        }
    }

    // Fungsi Hapus Chat (Tetap sama)
    fun deleteChat(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("chats").document(chatId).delete()
        }
    }
}