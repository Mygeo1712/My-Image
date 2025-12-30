// File: org.dm.petsociety.viewmodel.AdoptionViewModel.kt
package org.dm.petsociety.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Pet

class AdoptionViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _adoptionPets = MutableStateFlow<List<Pet>>(emptyList())
    val adoptionPets: StateFlow<List<Pet>> = _adoptionPets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var allPets = listOf<Pet>()

    init {
        fetchAllPets()
    }

    fun fetchAllPets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = auth.currentUser?.uid
                val snapshot = db.collection("pets").get().await()
                val pets = snapshot.toObjects(Pet::class.java)

                // FILTER:
                // 1. Bukan hewan milik user yang sedang login (jika user itu shelter)
                // 2. Status TIDAK SAMA DENGAN "Adopted" / "Teradopsi"
                allPets = pets.filter { pet ->
                    pet.ownerId != currentUserId &&
                            pet.status != "Adopted" &&
                            pet.status != "Teradopsi"
                }

                _adoptionPets.value = allPets

            } catch (e: Exception) {
                Log.e("AdoptionVM", "Error fetching pets: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterPets(query: String, typeFilter: String) {
        var filtered = allPets
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.breed.contains(query, ignoreCase = true)
            }
        }
        if (typeFilter != "Semua") {
            filtered = filtered.filter { it.type.equals(typeFilter, ignoreCase = true) }
        }
        _adoptionPets.value = filtered
    }
}