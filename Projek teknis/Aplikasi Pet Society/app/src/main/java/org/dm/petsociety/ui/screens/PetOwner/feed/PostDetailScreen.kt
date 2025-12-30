// File: org.dm.petsociety.ui.screens.petowner.feed.PostDetailScreen.kt (KOREKSI LENGKAP)

package org.dm.petsociety.ui.screens.petowner.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.* import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.dm.petsociety.model.Comment
import org.dm.petsociety.ui.screens.FeedPostCard
import org.dm.petsociety.ui.screens.formatTimestamp
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.CommentViewModel
import org.dm.petsociety.viewmodel.PostViewModel
import org.dm.petsociety.viewmodel.PostViewModelFactory
import org.dm.petsociety.viewmodel.CommentViewModelFactory // FIX: Import Factory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: String,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    // FIX KRITIS: Gunakan Factory untuk CommentViewModel
    val commentViewModel: CommentViewModel = viewModel(factory = CommentViewModelFactory(authViewModel))

    val currentPost by commentViewModel.post.collectAsState()
    val comments by commentViewModel.comments.collectAsState()

    val authState by authViewModel.authState.collectAsState()
    val currentUserId = authState.userId ?: ""
    val username = authState.userName

    val isLiked by commentViewModel.isLiked.collectAsState(initial = false)

    var commentInput by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        commentViewModel.loadPostAndComments(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Detail", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDarkNavy),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("Tulis komentar...", color = Color.Gray) },
                    modifier = Modifier.weight(1f).heightIn(min = 50.dp, max = 150.dp),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentTeal,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = PrimaryDarkNavy,
                        unfocusedTextColor = PrimaryDarkNavy
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            commentViewModel.sendComment(postId, commentInput, username)
                            commentInput = ""
                        }
                    },
                    enabled = commentInput.isNotBlank(),
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Kirim", modifier = Modifier.size(24.dp), tint = PrimaryDarkNavy)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryDarkNavy)
        ) {
            item {
                if (currentPost != null) {
                    FeedPostCard(
                        postText = currentPost!!.content,
                        username = currentPost!!.username,
                        imageUrl = currentPost!!.imageUrl,
                        currentLikes = currentPost!!.likesCount,
                        isLiked = isLiked,
                        initialComments = comments.size,
                        onLikeClick = { isCurrentlyLiked -> commentViewModel.toggleLike(postId, isCurrentlyLiked) },
                        onCommentClick = { /* Di detail screen, klik komentar tidak perlu navigasi */ }
                    )
                    Divider(thickness = 1.dp, color = AccentTeal.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 24.dp))
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentTeal)
                    }
                }
            }

            item {
                Text(
                    "Komentar (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = AccentTeal
                )
            }

            items(comments, key = { it.commentId }) { comment ->
                CommentItem(comment)
                Divider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}


@Composable
fun CommentItem(comment: Comment) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                comment.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AccentTeal
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                formatTimestamp(comment.timestamp),
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            comment.content,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}