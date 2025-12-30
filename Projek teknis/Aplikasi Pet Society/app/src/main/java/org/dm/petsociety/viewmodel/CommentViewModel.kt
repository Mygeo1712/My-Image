package org.dm.petsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Comment
import org.dm.petsociety.model.Post
import java.util.UUID
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentViewModel (private val authViewModel: AuthViewModel): ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val currentUserId = auth.currentUser?.uid

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()


    fun loadPostAndComments(postId: String) {
        db.collection("posts").document(postId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("CommentVM", "Listen post failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    _post.value = snapshot.toObject(Post::class.java)?.copy(postId = postId)
                    checkLikeStatus(postId)
                } else {
                    _post.value = null
                }
            }


        db.collection("posts").document(postId).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("CommentVM", "Listen comments failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val newComments = snapshot.documents.mapNotNull { document ->
                        document.toObject(Comment::class.java)?.copy(commentId = document.id)
                    }
                    _comments.value = newComments
                }
            }
    }

    private fun checkLikeStatus(postId: String) {
        if (currentUserId == null) {
            _isLiked.value = false
            return
        }
        db.collection("posts").document(postId).collection("likes").document(currentUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                _isLiked.value = snapshot.exists()
            }
            .addOnFailureListener {
                _isLiked.value = false
            }
    }

    fun sendComment(postId: String, content: String, username: String) {
        val user = auth.currentUser
        if (user == null || content.isBlank()) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val commentId = UUID.randomUUID().toString()
                    val newComment = Comment(
                        commentId = commentId,
                        postId = postId,
                        userId = user.uid,
                        username = username,
                        content = content,
                        timestamp = Timestamp.now()
                    )

                    db.collection("posts").document(postId).collection("comments").document(commentId)
                        .set(newComment).await()

                    // FIX: Perbarui commentsCount di dokumen Post utama (agar tampil di Feed Utama)
                    val postRef = db.collection("posts").document(postId)
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(postRef)
                        val newCommentsCount = (snapshot.getLong("commentsCount") ?: 0) + 1
                        transaction.update(postRef, "commentsCount", newCommentsCount)
                        null
                    }.await()

                } catch (e: Exception) {
                    Log.e("CommentVM", "Error sending comment: ${e.message}")
                }
            }
        }
    }

    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        val user = auth.currentUser ?: return
        val postRef = db.collection("posts").document(postId)
        val likeRef = postRef.collection("likes").document(user.uid)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (isCurrentlyLiked) {
                        likeRef.delete().await()
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(postRef)
                            val newLikesCount = (snapshot.getLong("likesCount") ?: 0) - 1
                            transaction.update(postRef, "likesCount", maxOf(0, newLikesCount))
                            null
                        }.await()
                        _isLiked.value = false
                    } else {
                        likeRef.set(mapOf("timestamp" to Timestamp.now(), "userId" to user.uid)).await()
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(postRef)
                            val newLikesCount = (snapshot.getLong("likesCount") ?: 0) + 1
                            transaction.update(postRef, "likesCount", newLikesCount)
                            null
                        }.await()
                        _isLiked.value = true
                    }
                } catch (e: Exception) {
                    Log.e("CommentVM", "Error toggling like: ${e.message}")
                }
            }
        }
    }
}