// File: org.dm.petsociety.ui.screens.petowner.profile.PetOwnerProfileScreen.kt

package org.dm.petsociety.ui.screens.petowner.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.dm.petsociety.model.Pet
import org.dm.petsociety.model.Post
import org.dm.petsociety.ui.screens.AppBottomBar
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.screens.AsyncImagePlaceholder
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.ProfileViewModel
import org.dm.petsociety.viewmodel.ProfileViewModelFactory


// Warna spesifik untuk profil
val CardProfile = Color(0xFF1E283A)
val TextLight = Color.White
val TextSecondary = Color.White.copy(alpha = 0.7f)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PetOwnerProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(authViewModel))
    val state by viewModel.state.collectAsState()
    val userPosts by viewModel.userPosts.collectAsState()
    val selectedPet by viewModel.selectedPet.collectAsState()

    var showEditUsernameDialog by remember { mutableStateOf(false) }
    var tempUsernameInput by remember { mutableStateOf(state.userName) }

    var showDeletePostDialog by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<Post?>(null) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Peliharaan (${state.pets.size})", "Postingan (${userPosts.size})")

    val currentRoute = navController.currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        viewModel.refreshProfile()
    }

    LaunchedEffect(state.userName) {
        tempUsernameInput = state.userName
    }

    // --- DIALOG KONFIRMASI HAPUS POSTINGAN ---
    if (showDeletePostDialog && postToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeletePostDialog = false; postToDelete = null },
            title = { Text("Hapus Postingan?", color = PrimaryDarkNavy, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus postingan ini?", color = PrimaryDarkNavy) },
            containerColor = Color.White,
            confirmButton = {
                TextButton(
                    onClick = {
                        postToDelete?.let { viewModel.deletePost(it.postId) }
                        showDeletePostDialog = false
                        postToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePostDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = { AppTopBar(title = "Profil Saya", navController = navController, showProfile = false) },
        containerColor = PrimaryDarkNavy,
        bottomBar = { AppBottomBar(navController = navController, currentRoute = "home_owner_profile") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryDarkNavy)
                .alpha(if (selectedPet != null) 0.5f else 1f)
                .clickable(enabled = selectedPet != null) { viewModel.selectPet(null) }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    // --- HEADER PROFIL ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardProfile)
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar dengan Border Accent
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(4.dp, AccentTeal, CircleShape)
                                .background(AccentTeal.copy(alpha = 0.3f))
                                .clickable { showEditUsernameDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.userName.firstOrNull()?.toString() ?: "?", fontSize = 48.sp, color = TextLight)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Nama dan Role
                        Text(
                            state.userName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = TextLight,
                            modifier = Modifier.clickable { showEditUsernameDialog = true }
                        )
                        Text(state.userRole, color = AccentTeal, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Lokasi dan Jumlah Pet
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, "Lokasi", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(state.userLocation, color = TextSecondary, fontSize = 12.sp)
                            }
                            Text("•", color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pets, "Jumlah Pet", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${state.pets.size} Hewan", color = TextSecondary, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tombol Kelola Peliharaan (Aksi Utama)
                        Button(
                            onClick = { navController.navigate("pet_list_screen") },
                            modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Kelola Peliharaan", fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // TOMBOL LOGOUT
                        OutlinedButton(
                            onClick = {
                                authViewModel.logout()
                                navController.navigate("landing") {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red.copy(alpha = 0.8f)),
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.8f))
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout", fontWeight = FontWeight.Bold)
                        }
                    }

                    // --- TAB ROW ---
                    TabRow(selectedTabIndex = selectedTabIndex, containerColor = PrimaryDarkNavy) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, color = if (selectedTabIndex == index) AccentTeal else TextSecondary) },
                                selectedContentColor = AccentTeal,
                                unselectedContentColor = TextSecondary
                            )
                        }
                    }
                }

                // --- KONTEN TAB ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 800.dp)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AccentTeal)
                        } else if (selectedTabIndex == 0) {
                            // TAB: GALERI HEWAN PELIHARAAN
                            if (state.pets.isEmpty()) {
                                Text(
                                    "Belum ada hewan yang terdaftar.",
                                    color = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    userScrollEnabled = false
                                ) {
                                    items(state.pets) { pet ->
                                        PetGridItem(pet = pet, onClick = { viewModel.selectPet(pet) })
                                    }
                                }
                            }
                        } else {
                            // TAB: POSTINGAN
                            if (userPosts.isEmpty()) {
                                Text(
                                    "Belum ada postingan yang diunggah.",
                                    color = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    userScrollEnabled = false
                                ) {
                                    items(userPosts) { post ->
                                        PostGridItem(
                                            post = post,
                                            onClick = { navController.navigate("post_detail/${post.postId}") },
                                            onLongClick = {
                                                postToDelete = post
                                                showDeletePostDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Edit Username (Tetap Sama)
    if (showEditUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showEditUsernameDialog = false },
            title = { Text("Ubah Nama Pengguna", color = PrimaryDarkNavy) },
            containerColor = Color.White,
            text = {
                OutlinedTextField(
                    value = tempUsernameInput,
                    onValueChange = { tempUsernameInput = it },
                    label = { Text("Nama Baru") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserName(tempUsernameInput)
                        showEditUsernameDialog = false
                    },
                    enabled = tempUsernameInput.isNotBlank() && tempUsernameInput != state.userName
                ) { Text("Simpan") }
            },
            dismissButton = {
                Button(onClick = { showEditUsernameDialog = false }) { Text("Batal") }
            }
        )
    }

    // Modal Detail Hewan Peliharaan (Tetap Sama)
    if (selectedPet != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            PetDetailModal(pet = selectedPet!!, onDismiss = { viewModel.selectPet(null) })
        }
    }
}

@Composable
fun PetGridItem(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33425F))
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImagePlaceholder(pet.profileImageUrl, isProfileGrid = true)
            Text(
                pet.name,
                color = TextLight,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PostGridItem(post: Post, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33425F))
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImagePlaceholder(post.imageUrl, isProfileGrid = true)
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(16.dp)
            )
        }
    }
}

@Composable
fun PetDetailModal(pet: Pet, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, AccentTeal, RoundedCornerShape(16.dp))
            .clickable(enabled = false) {},
        colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(pet.name, color = AccentTeal, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(pet.breed, color = TextSecondary, fontSize = 18.sp)

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(vertical = 16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF33425F))
            ) {
                AsyncImagePlaceholder(pet.profileImageUrl)
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow("Jenis", pet.type)
                DetailRow("Ras", pet.breed)
                DetailRow("Usia", "${pet.age} Tahun")
                DetailRow("Berat", "${pet.weight} Kg")
                DetailRow("Kelamin", pet.gender)
                Divider(color = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                Text("Catatan:", color = TextLight, fontWeight = FontWeight.SemiBold)
                Text(pet.notes ?: "-", color = TextSecondary, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
            ) {
                Text("Tutup", color = PrimaryDarkNavy)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 14.sp)
        Text(value, color = TextLight, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}