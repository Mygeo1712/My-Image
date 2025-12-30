// File: org.dm.petsociety.model.Comment.kt (BARU)

package org.dm.petsociety.model

import com.google.firebase.Timestamp
import androidx.annotation.Keep

@Keep
data class Comment(
    val commentId: String = "",
    val postId: String = "", // Tautan ke Postingan
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)