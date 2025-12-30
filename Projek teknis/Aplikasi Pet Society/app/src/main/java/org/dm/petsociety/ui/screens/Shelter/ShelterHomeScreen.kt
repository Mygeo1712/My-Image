// File: org.dm.petsociety.ui.screens.shelter.ShelterHomeScreen.kt

package org.dm.petsociety.ui.screens.shelter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import org.dm.petsociety.model.Pet
import org.dm.petsociety.viewmodel.PetWithRescuer
import org.dm.petsociety.viewmodel.ShelterViewModel
import org.dm.petsociety.ui.screens.common.ChatListScreen
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.ChatViewModel
import androidx.compose.material3.FabPosition


// --- WARNA TEMA ---
val DarkBg = Color(0xFF141927)
val CardBg = Color(0xFF1F2937)
val AccentGreen = Color(0xFF4CAF50)

@Composable
fun ShelterHomeScreen(
    navController: NavController,
    viewModel: ShelterViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val showFab = selectedTab == 0 || selectedTab == 2

    // Instansiasi ViewModels di scope Composable
    val authViewModel: AuthViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel() // Tetap inisialisasi untuk Tab Chat

    Scaffold(
        bottomBar = {
            ShelterBottomBar3Menu(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        },
        containerColor = DarkBg,

        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { navController.navigate("shelter_add_pet") },
                    containerColor = AccentGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
            } else if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = { navController.navigate("shelter_edit_profile") },
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ShelterFeedContent(navController, viewModel)
                1 -> {
                    ChatListScreen(
                        navController = navController,
                        viewModel = chatViewModel,
                        role = "Shelter"
                    )
                }
                2 -> ShelterProfileContent(
                    navController = navController,
                    viewModel = viewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun ShelterFeedContent(
    navController: NavController,
    viewModel: ShelterViewModel
) {
    val feedList by viewModel.communityFeed.collectAsState()
    val profile by viewModel.shelterProfile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCommunityFeed()
        viewModel.fetchUserProfile()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = if (profile.photoUrl.isNotEmpty()) profile.photoUrl else "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Halo, ${profile.name}! \uD83D\uDC4B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Temukan teman baru untuk diadopsi", color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (feedList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada postingan hewan.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feedList, key = { it.pet.petId }) { item ->
                    ShelterFeedCard(
                        item = item,
                        onClick = {
                            navController.navigate("shelter_pet_detail/${item.pet.petId}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShelterProfileContent(
    navController: NavController,
    viewModel: ShelterViewModel,
    authViewModel: AuthViewModel
) {
    val profile by viewModel.shelterProfile.collectAsState()
    val myPets by viewModel.myPets.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // State untuk Long Press Delete Pet
    var showDeletePetDialog by remember { mutableStateOf(false) }
    var petToDelete by remember { mutableStateOf<Pet?>(null) }

    // FIX NAVIGASI: Panggilan refresh data saat kembali ke Home
    // Ini menangani kasus navigasi Back dari Detail Pet kembali ke ShelterHomeScreen
    LaunchedEffect(navController.currentBackStackEntry) {
        // NavController.currentDestination tidak reliable, kita cek route
        val route = navController.currentBackStackEntry?.destination?.route
        if (route == "home_shelter") {
            viewModel.fetchMyPets()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
        viewModel.fetchMyPets()
    }

    // --- DIALOG KONFIRMASI HAPUS PET ---
    if (showDeletePetDialog && petToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeletePetDialog = false; petToDelete = null },
            title = { Text("Hapus Peliharaan?", color = Color(0xFF1E2A47), fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus ${petToDelete?.name}? Ini tidak dapat dibatalkan.", color = Color(0xFF1E2A47)) },
            containerColor = Color.White,
            confirmButton = {
                TextButton(
                    onClick = {
                        petToDelete?.let { viewModel.deletePet(it.petId) }
                        showDeletePetDialog = false
                        petToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Menggunakan Column dengan verticalScroll untuk menangani seluruh konten profil
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER PROFIL ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = if (profile.photoUrl.isNotEmpty()) profile.photoUrl else "https://via.placeholder.com/150",
                contentDescription = "Foto Profil",
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = profile.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Shelter/Rescuer", color = AccentGreen, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = profile.description, color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("shelter_edit_profile") },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    // LOGOUT & NAVIGASI
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

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Hewan Siap Adopsi (${myPets.size})", modifier = Modifier.padding(start = 16.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)

        if (myPets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada hewan yang diupload.", color = Color.Gray)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 500.dp)
                    .padding(horizontal = 16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(myPets, key = { it.petId }) { pet ->
                        ShelterPetGridItemForProfile(
                            pet = pet,
                            onClick = {
                                // Navigasi ke detail
                                navController.navigate("shelter_pet_detail/${pet.petId}")
                            },
                            onLongClick = { petToDel ->
                                petToDelete = petToDel
                                showDeletePetDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// Komponen Long Press Delete Pet
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShelterPetGridItemForProfile(
    pet: Pet,
    onClick: () -> Unit,
    onLongClick: (Pet) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick(pet) } // Long Press Trigger Delete
            )
    ) {
        Column {
            AsyncImage(
                model = pet.profileImageUrl ?: "",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(pet.name, fontWeight = FontWeight.Bold, maxLines = 1, color = Color.White)
                Text("${pet.breed}", fontSize = 12.sp, color = Color.LightGray, maxLines = 1)
            }
        }
    }
}

@Composable
fun ShelterFeedCard(
    item: PetWithRescuer,
    onClick: () -> Unit
) {
    val AccentGreen = Color(0xFF4CAF50)
    val isAvailable = item.pet.status == "Available" || item.pet.status == "Tersedia"
    val statusColor = if (isAvailable) Color(0xFF81C784) else Color(0xFFEF5350)
    val statusText = if (isAvailable) "Tersedia" else "Tdk Tersedia"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column {
            // Gambar Hewan
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                AsyncImage(
                    model = item.pet.profileImageUrl ?: "https://via.placeholder.com/300",
                    contentDescription = item.pet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // STATUS BADGE (KIRI ATAS)
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    color = if (isAvailable) Color(0xFF1B5E20) else Color(0xFF3E2723), // Latar belakang gelap
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = statusText.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Badge Gender (Pojok Kanan Atas)
                Surface(
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    color = if (item.pet.gender == "Jantan") Color(0xFFE3F2FD) else Color(0xFFFCE4EC),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (item.pet.gender == "Jantan") "♂" else "♀",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (item.pet.gender == "Jantan") Color.Blue else Color.Magenta,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Info Card
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.pet.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = item.pet.breed,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Info Rescuer yg Posting
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = AccentGreen,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.rescuerName.take(1).uppercase(), fontSize = 10.sp, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.rescuerName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF81C784),
                        maxLines = 1
                    )
                }
            }
        }
    }
}


@Composable
fun ShelterBottomBar3Menu(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF4CAF50)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Beranda") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = null) },
            label = { Text("Chat") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profil") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
        )
    }
}