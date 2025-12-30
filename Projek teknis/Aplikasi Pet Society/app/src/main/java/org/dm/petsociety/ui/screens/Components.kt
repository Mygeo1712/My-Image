// File: org.dm.petsociety.ui.screens.Components.kt (REVISI LENGKAP - Hapus Navigasi Expert/Dokter)

package org.dm.petsociety.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import org.dm.petsociety.R
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.FieldBackground
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.util.concurrent.TimeUnit
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale


// Warna yang konsisten
val BackgroundDark = PrimaryDarkNavy
val AccentColor = AccentTeal
val TextLight = Color.White
val TextSecondary = Color.White.copy(alpha = 0.7f)
val CardColor = Color(0xFF1E283A)


fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Baru saja"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}j"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}h"
        else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}


@Composable
fun AuthTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = FieldBackground,
        unfocusedContainerColor = FieldBackground,
        disabledContainerColor = FieldBackground,
        cursorColor = AccentTeal,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Red,
        focusedLabelColor = Color.Gray,
        unfocusedLabelColor = Color.Gray,
        focusedLeadingIconColor = AccentTeal,
        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.5f),
        focusedTrailingIconColor = AccentTeal,
        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.5f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    colors: TextFieldColors = AuthTextFieldColors()
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val visualTransformation = if (isPassword && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.5f)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = colors,
        leadingIcon = leadingIcon?.let {
            { Icon(it, contentDescription = null) }
        },
        trailingIcon = if (isPassword) {
            {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            }
        } else null
    )
}

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AccentTeal,
    contentColor: Color = Color.White,
    leadingIconRes: Int? = null,
    iconColor: Color = contentColor
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.Bold)
        }
    }
}


// --- KOMPONEN UI UMUM UNTUK HOME SCREENS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, navController: NavController, showProfile: Boolean = false) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, color = TextLight) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
        actions = {
            if (showProfile) {
                IconButton(onClick = {
                    navController.navigate("home_owner_profile")
                }) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = TextLight)
                }
            }
        }
    )
}

@Composable
fun AppBottomBar(navController: NavController, currentRoute: String) {
    NavigationBar(
        containerColor = CardColor,
        contentColor = AccentColor
    ) {
        // PERUBAHAN: Hapus "Dokter"
        val items = listOf("Feed", "Search", "Chat", "Profil")
        val icons = listOf(
            Icons.Filled.Home,
            Icons.Filled.Search,
            Icons.Filled.ChatBubble,
            Icons.Filled.AccountCircle
        )
        val routes = listOf(
            "home_owner_feed",
            "home_owner_search",
            "home_owner_chat",
            "home_owner_profile"
        )

        items.forEachIndexed { index, item ->
            val route = routes[index]
            val isSelected = currentRoute.contains(route)

            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                selected = isSelected,
                onClick = {
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentColor,
                    selectedTextColor = AccentColor,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary.copy(alpha = 0.6f),
                    indicatorColor = BackgroundDark
                )
            )
        }
    }
}


@Composable
fun GreetingCard(name: String, role: String, bgColor: Color = BackgroundDark) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 10.dp)
            .background(bgColor)
    ) {
        Text(
            text = "Halo, $name!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AccentColor
        )
        Text(
            text = "Selamat datang di Komunitas Pet Society.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
        color = TextLight
    )
}

@Composable
fun QuickActions(actions: List<String>, icons: List<ImageVector>, onActionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text("Menu Cepat", color = TextSecondary, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp))
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(actions.size) { index ->
                QuickActionButton(
                    text = actions[index],
                    icon = icons[index],
                    onClick = { onActionClick(actions[index]) }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = text, tint = AccentColor, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = TextLight,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FeedPostCard(
    postText: String,
    username: String = "Nama Pengguna",
    imageUrl: String? = null,
    timestamp: Timestamp = Timestamp.now(),
    currentLikes: Int,
    isLiked: Boolean, // Status Like yang di-hoist
    initialComments: Int,
    onLikeClick: (Boolean) -> Unit,
    onCommentClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Header Post (Avatar + Nama + Waktu)
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AccentColor.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(username.firstOrNull()?.toString() ?: "?", color = TextLight, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(username, fontWeight = FontWeight.SemiBold, color = TextLight)
                    Text(
                        formatTimestamp(timestamp), // WAKTU POSTING
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Foto/Video
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImagePlaceholder(imageUrl, isPostImage = true)
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Aksi dan Konten (Layout Gambar 2)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

                // BARIS Aksi (Like & Komentar)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Tombol Like
                    Icon(
                        // FIX KRITIS: MENGGUNAKAN PARAMETER isLiked UNTUK IKON
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        // FIX KRITIS: MENGGUNAKAN PARAMETER isLiked UNTUK WARNA
                        tint = if (isLiked) Color.Red else TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                // Meneruskan status like saat ini (isLiked) ke ViewModel untuk toggle
                                onLikeClick(isLiked)
                            }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(currentLikes.toString(), color = TextSecondary)

                    Spacer(modifier = Modifier.width(16.dp))

                    // 2. Tombol Komentar
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onCommentClick() }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(initialComments.toString(), color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // KONTEN Teks
                if (postText.isNotBlank()) {
                    Text(postText, color = TextLight.copy(alpha = 0.9f))
                }
            }
        }
    }
}


@Composable
fun AsyncImagePlaceholder(imageUrl: String?, isPostImage: Boolean = false, isProfileGrid: Boolean = false) {
    val modifier = when {
        isPostImage -> Modifier.fillMaxWidth().height(250.dp)
        isProfileGrid -> Modifier.aspectRatio(1f)
        else -> Modifier.fillMaxWidth().height(200.dp)
    }

    Box(
        modifier = modifier
            .clip(if (isPostImage) RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp) else RoundedCornerShape(8.dp))
            .background(CardColor.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrEmpty() || imageUrl == "https://via.placeholder.com/150/00FFFF/000000?text=PET") {
            // Placeholder Avatar/Pet generik (Gambar hijau/coklat di profil)
            Icon(Icons.Default.Pets, contentDescription = "Placeholder", tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Post Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}