// File: org.dm.petsociety.viewmodel.ProfileState.kt (BARU)

package org.dm.petsociety.viewmodel

import org.dm.petsociety.model.Pet

data class ProfileState(
    val isLoading: Boolean = false,
    val userName: String = "Ovan",
    val userRole: String = "Pet Owner",
    val userLocation: String = "Jakarta",
    val pets: List<Pet> = emptyList(),
    val errorMessage: String? = null
)