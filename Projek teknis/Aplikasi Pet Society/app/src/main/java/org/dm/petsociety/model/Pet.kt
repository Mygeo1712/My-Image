package org.dm.petsociety.model

import androidx.annotation.Keep

@Keep
data class Pet(
    val petId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = "Anjing",
    val breed: String = "",
    val age: Int = 0,
    val weight: Int = 0,
    val gender: String = "",
    val notes: String? = null,
    val profileImageUrl: String? = null,

    val status: String = "Available" // Default: Available (Tersedia)
)

object PetType {
    val options = listOf("Anjing", "Kucing", "Burung", "Lainnya")
}