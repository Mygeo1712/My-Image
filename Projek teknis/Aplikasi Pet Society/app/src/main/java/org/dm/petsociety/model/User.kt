package org.dm.petsociety.model
import androidx.annotation.Keep
// Data class ini WAJIB memiliki nilai default (= "")
// agar tidak crash saat membaca data dari Firebase Firestore.
@Keep
data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",

    // --- FIELD BARU UNTUK EXPERT ---
    val location: String = "",
    val specialization: String = ""
)