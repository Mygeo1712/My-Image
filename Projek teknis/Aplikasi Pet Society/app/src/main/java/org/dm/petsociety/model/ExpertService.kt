package org.dm.petsociety.model

import androidx.annotation.Keep

@Keep
data class ExpertService(
    val serviceId: String = "",
    val expertId: String = "",
    val serviceName: String = "",
    val category: String = "Medis",
    val description: String = "",
    val price: String = "",
    val location: String = "", // <-- Field Baru
    val imageUrl: String = "",
    val status: String = "Available" // Value: "Available" atau "Unavailable"
)

object ServiceCategory {
    val options = listOf("Medis", "Grooming", "Konsultasi", "Penitipan", "Lainnya")
}