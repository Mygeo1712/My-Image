// File: org.dm.petsociety.viewmodel.PostViewModel.kt

package org.dm.petsociety.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.dm.petsociety.model.Post
import org.dm.petsociety.network.CloudinaryClient
import java.util.UUID

// State untuk status upload
sealed class UploadState {
    data object Idle : UploadState()
    data object Uploading : UploadState()
    data class Error(val message: String) : UploadState()
    data object Success : UploadState()
}

class PostViewModel(private val authViewModel: AuthViewModel) : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // 1. STATE UNTUK MEMBACA POSTINGAN (Tetap ada, mungkin dibutuhkan di tempat lain)
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // 2. STATE UNTUK UPLOAD (Dibutuhkan oleh CreatePostScreen)
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    // --- FUNGSI MEMBACA DATA ---

    // HAPUS: loadPostsByCategory (Tidak lagi digunakan untuk Playdate)
    /*
    fun loadPostsByCategory(category: String) {
        viewModelScope.launch {
            db.collection("posts")
                .whereEqualTo("category", category)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("PostVM", "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        _posts.value = snapshot.toObjects(Post::class.java)
                    }
                }
        }
    }
    */

    // Fungsi load semua post (Untuk Feed Utama)
    fun loadAllPosts() {
        viewModelScope.launch {
            db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    if (snapshot != null) {
                        _posts.value = snapshot.toObjects(Post::class.java)
                    }
                }
        }
    }

    // --- FUNGSI UPLOAD DATA ---

    fun createPost(content: String, category: String = "Umum", bitmap: Bitmap?) {
        val authState = authViewModel.authState.value
        val userId = authState.userId
        val username = authState.userName

        if (userId == null || username == "Pengguna" || username.isBlank()) {
            _uploadState.update { UploadState.Error("Autentikasi gagal. Coba Login ulang.") }
            return
        }
        if (content.isBlank() && bitmap == null) {
            _uploadState.update { UploadState.Error("Konten atau gambar tidak boleh kosong.") }
            return
        }

        _uploadState.update { UploadState.Uploading }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var imageUrl: String? = null
                    if (bitmap != null) {
                        imageUrl = CloudinaryClient.uploadImage(bitmap)
                    }

                    val postId = UUID.randomUUID().toString()
                    val newPost = Post(
                        postId = postId,
                        userId = userId,
                        username = username,
                        userPhotoUrl = "",
                        content = content,
                        imageUrl = imageUrl,
                        category = "Umum", // FIX: Category selalu "Umum" setelah revisi CreatePostScreen
                        likesCount = 0,
                        timestamp = Timestamp.now()
                    )

                    db.collection("posts").document(postId).set(newPost).await()
                    _uploadState.update { UploadState.Success }

                } catch (e: Exception) {
                    Log.e("PostVM", "Gagal upload: ${e.message}", e)
                    _uploadState.update { UploadState.Error("Gagal Unggah: ${e.localizedMessage}") }
                }
            }
        }
    }

    fun clearState() {
        _uploadState.value = UploadState.Idle
    }
}