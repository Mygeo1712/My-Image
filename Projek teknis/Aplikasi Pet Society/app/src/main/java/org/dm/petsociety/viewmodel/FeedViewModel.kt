package org.dm.petsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Post
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore
import org.dm.petsociety.data.FeedRepository
import com.google.firebase.Timestamp

data class FeedState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FeedViewModel(private val authViewModel: AuthViewModel) : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val currentUserId = auth.currentUser?.uid

    private val repository = FeedRepository(db)

    // Variabel untuk menyimpan listener liked posts
    private var likedPostsListener: ListenerRegistration? = null

    private val _feedState = MutableStateFlow(FeedState())
    val feedState: StateFlow<FeedState> = _feedState.asStateFlow()

    val posts: StateFlow<List<Post>> = repository.getPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentUsername: StateFlow<String> = authViewModel.authState.map { it.userName }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authViewModel.authState.value.userName
        )

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    private val _isProcessingLike = MutableStateFlow(false)
    val isProcessingLike: StateFlow<Boolean> = _isProcessingLike.asStateFlow()

    init {
        loadLikedPosts()
    }

    override fun onCleared() {
        super.onCleared()
        // FIX: Hapus listener saat ViewModel dibersihkan
        likedPostsListener?.remove()
    }

    private fun loadLikedPosts() {
        if (currentUserId == null) return

        // Hapus listener lama sebelum membuat yang baru
        likedPostsListener?.remove()

        // Listener Collection Group Query
        likedPostsListener = db.collectionGroup("likes")
            .whereEqualTo("userId", currentUserId!!)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    if (e is com.google.firebase.firestore.FirebaseFirestoreException &&
                        e.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                        Log.e("FeedVM", "INDEX LIKES BELUM SIAP! SILAKAN CEK KONSOLE FIRESTORE.")
                    } else {
                        Log.w("FeedVM", "Listen liked posts failed due to network/other.", e)
                    }
                    _likedPostIds.value = emptySet()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val likedIds = snapshot.documents.mapNotNull { document ->
                        document.reference.parent.parent?.id
                    }.toSet()
                    _likedPostIds.value = likedIds
                    Log.d("FeedVM", "Liked Posts Synced. Count: ${likedIds.size}")
                }
            }
    }


    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        val user = auth.currentUser
        if (user == null || _isProcessingLike.value) {
            return
        }

        _isProcessingLike.value = true

        val postRef = db.collection("posts").document(postId)
        val likeRef = postRef.collection("likes").document(user.uid)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Cek status like yang sebenarnya sebelum operasi dimulai
                    val actualLikedStatus = likeRef.get().await().exists()

                    if (actualLikedStatus) {
                        // Hapus Like
                        likeRef.delete().await()
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(postRef)
                            val newLikesCount = (snapshot.getLong("likesCount") ?: 0) - 1
                            transaction.update(postRef, "likesCount", maxOf(0, newLikesCount))
                            null
                        }.await()

                    } else {
                        // Tambah Like
                        likeRef.set(mapOf("timestamp" to Timestamp.now(), "userId" to user.uid)).await()
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(postRef)
                            val newLikesCount = (snapshot.getLong("likesCount") ?: 0) + 1
                            transaction.update(postRef, "likesCount", newLikesCount)
                            null
                        }.await()
                    }

                } catch (e: Exception) {
                    Log.e("FeedVM", "Error toggling like: ${e.message}")
                } finally {
                    _isProcessingLike.value = false
                }
            }
        }
    }
}