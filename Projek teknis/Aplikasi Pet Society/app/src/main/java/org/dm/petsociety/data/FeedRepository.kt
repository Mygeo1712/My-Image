package org.dm.petsociety.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.dm.petsociety.model.Post

// Kelas ini menangani interaksi data feed dengan Firestore
class FeedRepository(
    private val db: FirebaseFirestore // Menerima instance Firestore
) {
    fun getPosts(): Flow<List<Post>> = callbackFlow {
        val subscription = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("FeedRepository", "Listen failed.", e)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(Post::class.java)?.copy(postId = document.id)
                        } catch (e: Exception) {
                            Log.e("FeedRepository", "Error mapping post: ${e.message}")
                            null
                        }
                    }
                    trySend(posts)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { subscription.remove() }
    }
}