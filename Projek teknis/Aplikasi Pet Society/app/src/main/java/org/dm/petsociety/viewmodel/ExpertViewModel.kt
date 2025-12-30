package org.dm.petsociety.viewmodel

import android.graphics.Bitmap
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
import org.dm.petsociety.model.ExpertService
import org.dm.petsociety.model.User // Import User model
import org.dm.petsociety.network.CloudinaryClient
import java.util.UUID

data class ServiceWithExpert(
    val service: ExpertService,
    val expertName: String,
    val expertPhoto: String
)

class ExpertViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _expertFeed = MutableStateFlow<List<ServiceWithExpert>>(emptyList())
    val expertFeed: StateFlow<List<ServiceWithExpert>> = _expertFeed

    private val _myServices = MutableStateFlow<List<ExpertService>>(emptyList())
    val myServices: StateFlow<List<ExpertService>> = _myServices

    // Gunakan Model User Langsung agar lengkap (ada location & specialization)
    private val _currentUserProfile = MutableStateFlow(User())
    val currentUserProfile: StateFlow<User> = _currentUserProfile

    init {
        fetchExpertProfile()
        fetchExpertFeed()
        fetchMyServices()
    }

    // 1. FETCH PROFILE (LENGKAP)
    fun fetchExpertProfile() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                val doc = db.collection("users").document(uid).get().await()
                val user = doc.toObject(User::class.java)
                if (user != null) {
                    _currentUserProfile.value = user
                }
            } catch (e: Exception) { }
        }
    }

    // 2. UPDATE PROFIL (NAMA, BIO, LOKASI, SPESIALISASI)
    fun updateExpertProfile(name: String, bio: String, location: String, specialization: String, imageBitmap: Bitmap?) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                var finalPhotoUrl = _currentUserProfile.value.profileImageUrl
                if (imageBitmap != null) {
                    finalPhotoUrl = withContext(Dispatchers.IO) {
                        CloudinaryClient.uploadImage(imageBitmap)
                    }
                }

                val updates = mapOf(
                    "name" to name,
                    "bio" to bio,
                    "location" to location,
                    "specialization" to specialization,
                    "profileImageUrl" to finalPhotoUrl
                )

                db.collection("users").document(uid).update(updates).await()
                fetchExpertProfile() // Refresh data lokal
                _uiState.value = UiState.Success("Profil berhasil diperbarui!")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal update profil")
            }
        }
    }

    // 3. FETCH FEED (HANYA YG AVAILABLE)
    fun fetchExpertFeed() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("expert_services").get().await()
                val allServices = snapshot.toObjects(ExpertService::class.java)

                // FILTER: Hanya tampilkan yang statusnya "Available"
                val availableServices = allServices.filter { it.status == "Available" }

                val feedItems = availableServices.map { service ->
                    val userDoc = db.collection("users").document(service.expertId).get().await()
                    val name = userDoc.getString("name") ?: "Ahli Hewan"
                    val photo = userDoc.getString("profileImageUrl") ?: ""
                    ServiceWithExpert(service, name, photo)
                }
                _expertFeed.value = feedItems
            } catch (e: Exception) { }
        }
    }

    // 4. FETCH MY SERVICES (SEMUA, TERMASUK UNAVAILABLE)
    fun fetchMyServices() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val snapshot = db.collection("expert_services").whereEqualTo("expertId", uid).get().await()
            _myServices.value = snapshot.toObjects(ExpertService::class.java)
        }
    }

    // 5. UPDATE STATUS LAYANAN (AVAILABLE <-> UNAVAILABLE)
    fun updateServiceStatus(serviceId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                db.collection("expert_services").document(serviceId)
                    .update("status", newStatus)
                    .await()

                fetchMyServices() // Refresh di Profil (agar warna berubah)
                fetchExpertFeed() // Refresh di Beranda (agar hilang/muncul)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Gagal update status")
            }
        }
    }

    // ... (Fungsi addService, updateService, deleteService yg lama TETAP SAMA, tidak perlu dihapus) ...
    // Pastikan fungsi-fungsi CRUD lama Anda masih ada di bawah ini.

    fun addService(title: String, category: String, price: String, description: String, imageBitmap: Bitmap?) {
        // ... (Logic lama) ...
        // COPY DARI KODE SEBELUMNYA ATAU BIARKAN JIKA SUDAH ADA
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                var imageUrl = ""
                if (imageBitmap != null) {
                    imageUrl = withContext(Dispatchers.IO) { CloudinaryClient.uploadImage(imageBitmap) }
                }
                val newId = UUID.randomUUID().toString()
                val newService = ExpertService(
                    serviceId = newId, expertId = uid, serviceName = title,
                    category = category, price = price, description = description,
                    imageUrl = imageUrl, status = "Available"
                )
                db.collection("expert_services").document(newId).set(newService).await()
                _uiState.value = UiState.Success("Layanan berhasil diposting!")
                fetchMyServices()
                fetchExpertFeed()
            } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Gagal") }
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            db.collection("expert_services").document(serviceId).delete().await()
            fetchMyServices()
            fetchExpertFeed()
        }
    }

    fun updateService(serviceId: String, name: String, category: String, price: String, description: String, location: String, imageBitmap: Bitmap?, currentImageUrl: String) {
        // ... (Logic lama) ...
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                var finalImageUrl = currentImageUrl
                if (imageBitmap != null) {
                    finalImageUrl = withContext(Dispatchers.IO) { CloudinaryClient.uploadImage(imageBitmap) }
                }
                val updateData = mapOf("serviceName" to name, "category" to category, "price" to price, "description" to description, "location" to location, "imageUrl" to finalImageUrl)
                db.collection("expert_services").document(serviceId).update(updateData).await()
                _uiState.value = UiState.Success("Diperbarui!")
                fetchMyServices(); fetchExpertFeed()
            } catch (e: Exception) { _uiState.value = UiState.Error(e.message ?: "Gagal") }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}