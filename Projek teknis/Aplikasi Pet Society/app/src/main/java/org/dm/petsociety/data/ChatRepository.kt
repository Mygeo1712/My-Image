package org.dm.petsociety.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Chat
import org.dm.petsociety.model.ChatMessage

class ChatRepository(private val db: FirebaseFirestore) {

    fun getChats(currentUserId: String): Flow<List<Chat>> = callbackFlow {
        // FIX KRITIS: Menghapus orderBy() untuk mengatasi FAILED_PRECONDITION
        val chatsRef = db.collection("chats")
            .whereArrayContains("participants", currentUserId)
        // .orderBy("timestamp", Query.Direction.DESCENDING) // DIHAPUS
        // .orderBy("__name__", Query.Direction.ASCENDING)   // DIHAPUS

        val subscription = chatsRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("ChatRepo", "Listen chats failed.", e)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val chats = snapshot.documents.mapNotNull { document ->
                    document.toObject(Chat::class.java)?.copy(chatId = document.id)
                }
                trySend(chats)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { subscription.remove() }
    }

    fun getMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val messagesRef = db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val subscription = messagesRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("ChatRepo", "Listen messages failed.", e)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val messages = snapshot.documents.mapNotNull { document ->
                    document.toObject(ChatMessage::class.java)?.copy(messageId = document.id)
                }
                trySend(messages)
            }
        }

        awaitClose { subscription.remove() }
    }

    suspend fun findOrCreatePrivateChat(currentUserId: String, otherUserId: String, currentUserName: String, otherUserName: String): String {
        val participants = listOf(currentUserId, otherUserId).sorted()

        val existingChat = db.collection("chats")
            .whereEqualTo("participants", participants)
            .whereEqualTo("chatIsGroup", false)
            .limit(1)
            .get().await()

        if (existingChat.documents.isNotEmpty()) {
            return existingChat.documents.first().id
        }

        val newChatId = db.collection("chats").document().id
        val newChat = Chat(
            chatId = newChatId,
            participants = participants,
            chatIsGroup = false,
            companionId = otherUserId,
            companionName = otherUserName,
            lastMessage = "Mulai percakapan...",
            timestamp = Timestamp.now()
        )
        db.collection("chats").document(newChatId).set(newChat).await()
        return newChatId
    }
}