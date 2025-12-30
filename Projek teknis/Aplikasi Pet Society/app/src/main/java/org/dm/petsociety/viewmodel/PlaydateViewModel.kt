// File: org.dm.petsociety.viewmodel.PlaydateViewModel.kt

package org.dm.petsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Pet
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dm.petsociety.model.PetType

data class PlaydateState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val filterType: String = PetType.options.first(), // Default: Anjing
    val errorMessage: String? = null
)

class PlaydateViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val currentUserId = auth.currentUser?.uid

    private val _state = MutableStateFlow(PlaydateState())
    val state: StateFlow<PlaydateState> = _state.asStateFlow()

    private val allPets = MutableStateFlow<List<Pet>>(emptyList())

    // --- BARU: State Hewan yang dipilih untuk ditampilkan di modal detail ---
    private val _selectedPetDetail = MutableStateFlow<Pet?>(null)
    val selectedPetDetail: StateFlow<Pet?> = _selectedPetDetail.asStateFlow()


    init {
        loadAllPets()
        setupFiltering()
    }

    private fun loadAllPets() {
        // Mendengarkan semua dokumen 'pets' dari semua subkoleksi 'users/{userId}/pets'
        // Memerlukan Firestore Collection Group Query Index!
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            try {
                // Ambil semua dokumen 'pets' dari semua pengguna
                val snapshot = db.collectionGroup("pets").get().await()

                val petList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Pet::class.java)?.copy(
                        // Filter keluar hewan milik pengguna saat ini
                        petId = document.id,
                        ownerId = document.reference.parent.parent?.id ?: ""
                    )
                }.filter {
                    // Filter: Hanya Pet Owner (OwnerId tidak kosong dan bukan Pet milik sendiri)
                    it.ownerId != currentUserId && it.ownerId.isNotEmpty()
                }

                allPets.value = petList
            } catch (e: Exception) {
                Log.e("PlaydateVM", "Error loading all pets: ${e.message}")
                _state.update { it.copy(errorMessage = "Gagal memuat daftar hewan: ${e.message}") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    // Gunakan Flow.combine untuk menggabungkan data mentah dan filter
    private fun setupFiltering() {
        viewModelScope.launch {
            allPets.combine(_state) { pets, state ->
                // Filter berdasarkan tipe hewan yang dipilih
                pets.filter { it.type == state.filterType }
            }.collect { filteredPets ->
                _state.update { it.copy(pets = filteredPets) }
            }
        }
    }

    fun setFilterType(type: String) {
        _state.update { it.copy(filterType = type) }
    }

    // --- BARU: Fungsi untuk memilih/menutup detail hewan ---
    fun selectPetForDetail(pet: Pet?) {
        _selectedPetDetail.value = pet
    }

    // --- BARU: Fungsi Fetch Nama Owner untuk Detail (Karena field ownerId ada di Pet) ---
    suspend fun getOwnerName(ownerId: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val userDoc = db.collection("users").document(ownerId).get().await()
            userDoc.getString("name") ?: "Pet Owner"
        } catch (e: Exception) {
            "Pet Owner"
        }
    }
}