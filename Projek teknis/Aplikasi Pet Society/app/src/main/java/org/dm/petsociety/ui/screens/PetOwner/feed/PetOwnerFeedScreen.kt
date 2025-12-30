// File: org.dm.petsociety.ui.screens.petowner.feed.PetOwnerFeedScreen.kt

package org.dm.petsociety.ui.screens.petowner.feed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.screens.AppBottomBar
import org.dm.petsociety.ui.screens.GreetingCard
import org.dm.petsociety.ui.screens.SectionHeader
import org.dm.petsociety.ui.screens.FeedPostCard
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.FeedViewModel
import org.dm.petsociety.viewmodel.FeedViewModelFactory
import org.dm.petsociety.model.Post


@Composable
fun PetOwnerFeedScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // FeedViewModel menggunakan factory karena membutuhkan AuthVM
    val feedViewModel: FeedViewModel = viewModel(factory = FeedViewModelFactory(authViewModel))

    // HAPUS: val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(authViewModel))

    // FIX KRITIS: Langsung ambil AuthState untuk nama dan peran
    val authState by authViewModel.authState.collectAsState()

    // Data feed
    val posts by feedViewModel.posts.collectAsState()
    val likedPostIds by feedViewModel.likedPostIds.collectAsState()
    val isProcessingLike by feedViewModel.isProcessingLike.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "Community Feed", navController = navController, showProfile = true) },
        containerColor = PrimaryDarkNavy,
        bottomBar = { AppBottomBar(navController = navController, currentRoute = "home_owner_feed") },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // postViewModel.clearState() // HAPUS pemanggilan ini
                    // Navigasi ke Create Post
                    navController.navigate("create_post_screen")
                },
                containerColor = AccentTeal,
                contentColor = PrimaryDarkNavy
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Upload Post")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDarkNavy)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 70.dp)
        ) {
            item {
                // MENGGUNAKAN AuthState yang sudah di-collect
                GreetingCard(
                    name = authState.userName, // <-- Ini pasti terikat ke instance AuthVM Activity
                    role = authState.userRole,
                    bgColor = PrimaryDarkNavy
                )
            }

            item { SectionHeader("Apa yang Baru dari Komunitas?") }

            if (posts.isEmpty()) {
                item {
                    Text(
                        "Belum ada postingan. Unggah yang pertama!",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                items(posts, key = { it.postId }) { post ->

                    val isPostLiked = likedPostIds.contains(post.postId)

                    FeedPostCard(
                        postText = post.content,
                        username = post.username,
                        imageUrl = post.imageUrl,
                        timestamp = post.timestamp ?: Timestamp(0, 0),
                        currentLikes = post.likesCount,
                        isLiked = isPostLiked,
                        initialComments = post.commentsCount,
                        onLikeClick = { isCurrentlyLiked ->
                            if (!isProcessingLike) {
                                feedViewModel.toggleLike(post.postId, isCurrentlyLiked)
                            }
                        },
                        onCommentClick = {
                            navController.navigate("post_detail/${post.postId}")
                        }
                    )
                }
            }
        }
    }
}