package org.dm.petsociety.model

import com.google.firebase.Timestamp
import androidx.annotation.Keep

@Keep
data class Chat(
    val chatId: String = "",
    val participants: List<String> = emptyList(), // List of User UIDs
    val lastMessage: String = "Mulai chat baru",
    val timestamp: Timestamp = Timestamp.now(),
    val chatIsGroup: Boolean = false,
    val groupName: String? = null,
    val groupType: String? = null,
    val companionName: String? = null,
    val companionId: String? = null
)
@Keep
data class ChatMessage(
    val messageId: String = "",
    val chatId: String = "",
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)