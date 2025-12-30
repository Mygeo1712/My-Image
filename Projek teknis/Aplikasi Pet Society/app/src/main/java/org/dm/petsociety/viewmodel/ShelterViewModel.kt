// File: org.dm.petsociety.viewmodel.ShelterViewModel.kt

package org.dm.petsociety.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.dm.petsociety.model.Pet
import org.dm.petsociety.network.CloudinaryClient
import java.util.UUID

// Data Class Helper: Menggabungkan Hewan & Nama Pemiliknya
data class PetWithRescuer(
    val pet: Pet,
    val rescuerName: String
)

data class ShelterProfile(
    val name: String = "",
    val description: String = "",
    val photoUrl: String = ""
)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

class ShelterViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // State Feed Komunitas (Semua hewan)
    private val _communityFeed = MutableStateFlow<List<PetWithRescuer>>(emptyList())
    val communityFeed: StateFlow<List<PetWithRescuer>> = _communityFeed

    // State Hewan Saya (Untuk Profil)
    private val _myPets = MutableStateFlow<List<Pet>>(emptyList())
    val myPets: StateFlow<List<Pet>> = _myPets

    // State Profil User (Untuk Header Halo)
    private val _shelterProfile = MutableStateFlow(ShelterProfile())
    val shelterProfile: StateFlow<ShelterProfile> = _shelterProfile

    init {
        fetchUserProfile()
        fetchCommunityFeed() // Ambil feed saat init
        fetchMyPets()
    }

    // --- 1. FEED KOMUNITAS (BERANDA) ---
    fun fetchCommunityFeed() {
        viewModelScope.launch {
            try {
                // Ambil semua hewan yang statusnya BUKAN Teradopsi
                val snapshot = db.collection("pets").get().await()
                val allPets = snapshot.toObjects(Pet::class.java)
                    .filter { it.status != "Adopted" && it.status != "Teradopsi" }

                // Ambil nama pemilik untuk setiap hewan (Mapping)
                val feedItems = allPets.map { pet ->
                    val ownerName = try {
                        val userDoc = db.collection("users").document(pet.ownerId).get().await()
                        userDoc.getString("name") ?: "Rescuer"
                    } catch (e: Exception) {
                        "Rescuer"
                    }
                    PetWithRescuer(pet, ownerName)
                }

                _communityFeed.value = feedItems
            } catch (e: Exception) {
                Log.e("ShelterVM", "Error fetch feed: ${e.message}")
            }
        }
    }

    // --- 2. PROFIL & USER ---
    fun fetchUserProfile() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                val doc = db.collection("users").document(uid).get().await()
                _shelterProfile.value = ShelterProfile(
                    name = doc.getString("name") ?: "Shelter",
                    description = doc.getString("description") ?: "",
                    photoUrl = doc.getString("photoUrl") ?: ""
                )
            } catch (e: Exception) { }
        }
    }

    // --- 3. CRUD HEWAN ---
    fun fetchMyPets() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val snapshot = db.collection("pets").whereEqualTo("ownerId", uid).get().await()
            _myPets.value = snapshot.toObjects(Pet::class.java)
        }
    }

    fun addShelterPet(
        name: String, type: String, breed: String, age: String,
        weight: String, gender: String, notes: String, imageBitmap: Bitmap?
    ) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                var imageUrl: String? = null
                if (imageBitmap != null) {
                    imageUrl = withContext(Dispatchers.IO) { CloudinaryClient.uploadImage(imageBitmap) }
                }

                val newPetId = UUID.randomUUID().toString()
                val newPet = Pet(
                    petId = newPetId, ownerId = uid, name = name, type = type,
                    breed = breed, age = age.toIntOrNull()?:0, weight = weight.toIntOrNull()?:0,
                    gender = gender, notes = notes, profileImageUrl = imageUrl
                )
                db.collection("pets").document(newPetId).set(newPet).await()
                _uiState.value = UiState.Success("Berhasil ditambah!")
                fetchMyPets() // Refresh my pets
                fetchCommunityFeed() // Refresh feed
            } catch (e: Exception) { _uiState.value = UiState.Error("Error: ${e.message}") }
        }
    }

    fun updateShelterPet(petId: String, name: String, type: String, breed: String, age: String, weight: String, gender: String, notes: String, imageBitmap: Bitmap?, currentImageUrl: String?) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                var finalImageUrl = currentImageUrl
                if (imageBitmap != null) {
                    finalImageUrl = withContext(Dispatchers.IO) { CloudinaryClient.uploadImage(imageBitmap) }
                }
                val updateData = mapOf("name" to name, "type" to type, "breed" to breed, "age" to (age.toIntOrNull() ?: 0), "weight" to (weight.toIntOrNull() ?: 0), "gender" to gender, "notes" to notes, "profileImageUrl" to finalImageUrl)
                db.collection("pets").document(petId).update(updateData).await()
                _uiState.value = UiState.Success("Diperbarui!")
                fetchMyPets()
                fetchCommunityFeed()
            } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Gagal") }
        }
    }

    fun deletePet(petId: String) {
        viewModelScope.launch {
            db.collection("pets").document(petId).delete().await()
            fetchMyPets()
            fetchCommunityFeed()
        }
    }

    fun updateProfile(name: String, description: String, imageBitmap: Bitmap?) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                var finalPhoto = _shelterProfile.value.photoUrl
                if (imageBitmap != null) { finalPhoto = withContext(Dispatchers.IO) { CloudinaryClient.uploadImage(imageBitmap) } }
                val map = mapOf("name" to name, "description" to description, "photoUrl" to finalPhoto)
                db.collection("users").document(uid).update(map).await()
                fetchUserProfile()
                _uiState.value = UiState.Success("Profil Updated")
            } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Error") }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}