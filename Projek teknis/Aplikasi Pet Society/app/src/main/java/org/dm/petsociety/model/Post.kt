package org.dm.petsociety.model

import com.google.firebase.Timestamp
import androidx.annotation.Keep

@Keep
data class Post(
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    // Tambahan baru agar profil user muncul
    val userPhotoUrl: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    // TAMBAHAN WAJIB untuk fitur Playdate
    val category: String = "Umum",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
)