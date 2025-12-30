// File: org.dm.petsociety.viewmodel.ProfileViewModel.kt (KOREKSI LENGKAP - TAMBAHKAN updatePet)

package org.dm.petsociety.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Pet
import android.util.Log
import org.dm.petsociety.model.Post
import org.dm.petsociety.network.CloudinaryClient
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import androidx.navigation.NavController // Tidak digunakan langsung, tapi diimpor untuk konsistensi

// FIX KRITIS: Menerima AuthViewModel
class ProfileViewModel(private val authViewModel: AuthViewModel) : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val userId = auth.currentUser?.uid

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()


    init {
        loadUserProfile()
        loadUserPosts()
    }

    // FIX AKSES: Wrapper publik untuk dipanggil dari Composable
    fun refreshProfile() {
        loadUserProfile()
    }

    private fun loadUserPosts() {
        if (userId == null) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val snapshot = db.collection("posts")
                        .whereEqualTo("userId", userId)
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(9)
                        .get().await()

                    _userPosts.value = snapshot.documents.mapNotNull { document ->
                        document.toObject(Post::class.java)?.copy(postId = document.id)
                    }
                } catch (e: Exception) {
                    Log.e("ProfileVM", "Error loading user posts: ${e.message}")
                }
            }
        }
    }


    fun loadUserProfile() { // Dibuat publik untuk dipanggil langsung
        if (userId == null) {
            _state.update { it.copy(errorMessage = "User not logged in.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                try {
                    val userDoc = db.collection("users").document(userId).get().await()

                    // Mengambil pets dari subkoleksi
                    val petsCollectionRef = db.collection("users").document(userId).collection("pets")
                    val petSnapshot = petsCollectionRef.get().await()

                    val pets = petSnapshot.documents.mapNotNull { document ->
                        // Memastikan PetId dan OwnerId disalin saat pemetaan.
                        document.toObject(Pet::class.java)?.copy(petId = document.id, ownerId = userId)
                    }

                    Log.d("ProfileVM", "Pets loaded count: ${pets.size}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            userName = userDoc.getString("name") ?: it.userName,
                            userRole = userDoc.getString("role") ?: it.userRole,
                            pets = pets,
                            userLocation = userDoc.getString("location") ?: "Jakarta" // Ambil lokasi dari userDoc
                        )
                    }
                } catch (e: Exception) {
                    Log.e("ProfileVM", "Error loading profile: ${e.message}")
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal memuat data profil.") }
                }
            }
        }
    }

    fun updateUserName(newName: String) {
        if (userId == null || newName.isBlank()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    db.collection("users").document(userId).update("name", newName).await()

                    authViewModel.fetchUserAndRole(userId) // <-- Membutuhkan AuthViewModel

                    updatePostUsername(newName)

                    loadUserProfile()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = "Gagal memperbarui nama: ${e.message}") }
                }
            }
        }
    }

    private suspend fun updatePostUsername(newName: String) {
        if (userId == null) return

        withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get().await()

                if (snapshot.documents.isNotEmpty()) {
                    val batch = db.batch()
                    snapshot.documents.forEach { document ->
                        batch.update(document.reference, "username", newName)
                    }
                    batch.commit().await()
                    Log.d("ProfileVM", "Successfully updated username in ${snapshot.documents.size} posts.")
                }

                loadUserPosts()

            } catch (e: Exception) {
                Log.e("ProfileVM", "Failed to update username in old posts: ${e.message}")
            }
        }
    }

    fun addPet(pet: Pet, bitmap: Bitmap?) {
        if (userId == null) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                try {
                    var imageUrl: String? = null

                    if (bitmap != null) {
                        Log.d("ProfileVM", "Starting image upload for new pet.")
                        imageUrl = CloudinaryClient.uploadImage(bitmap)
                    } else {
                        imageUrl = "https://via.placeholder.com/150/00FFFF/000000?text=PET"
                    }

                    val petId = UUID.randomUUID().toString()
                    val petWithData = pet.copy(
                        petId = petId,
                        ownerId = userId,
                        profileImageUrl = imageUrl
                    )

                    db.collection("users").document(userId).collection("pets").document(petId)
                        .set(petWithData).await()

                    delay(500) // Delay sinkronisasi

                    _state.update { it.copy(isLoading = false) }

                    loadUserProfile()
                    loadUserPosts()

                } catch (e: Exception) {
                    Log.e("ProfileVM", "Gagal menambah hewan: ${e.message}")
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal menambah hewan: ${e.message}") }
                }
            }
        }
    }

    // --- FUNGSI BARU UNTUK EDIT PET ---
    fun updatePet(pet: Pet, bitmap: Bitmap?) {
        if (userId == null || pet.petId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                try {
                    var finalImageUrl = pet.profileImageUrl

                    // 1. Upload gambar baru jika ada
                    if (bitmap != null) {
                        finalImageUrl = CloudinaryClient.uploadImage(bitmap)
                    }

                    // 2. Buat map data update
                    val updateData = mapOf(
                        "name" to pet.name,
                        "type" to pet.type,
                        "breed" to pet.breed,
                        "age" to pet.age,
                        "weight" to pet.weight,
                        "gender" to pet.gender,
                        "notes" to pet.notes,
                        "profileImageUrl" to finalImageUrl
                    )

                    // 3. Update dokumen di Firestore
                    db.collection("users").document(userId).collection("pets").document(pet.petId)
                        .update(updateData).await()

                    delay(500)

                    _state.update { it.copy(isLoading = false) }
                    loadUserProfile() // Refresh UI

                } catch (e: Exception) {
                    Log.e("ProfileVM", "Gagal mengupdate hewan: ${e.message}")
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal mengupdate hewan: ${e.message}") }
                }
            }
        }
    }
    // ------------------------------------

    fun deletePet(petId: String) {
        if (userId == null) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    db.collection("users").document(userId).collection("pets").document(petId).delete().await()
                    loadUserProfile()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = "Gagal menghapus hewan: ${e.message}") }
                }
            }
        }
    }

    fun deletePost(postId: String) {
        if (userId == null) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    db.collection("posts").document(postId).delete().await()
                    loadUserPosts()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = "Gagal menghapus postingan: ${e.message}") }
                }
            }
        }
    }

    fun selectPet(pet: Pet?) {
        _selectedPet.value = pet
    }
}