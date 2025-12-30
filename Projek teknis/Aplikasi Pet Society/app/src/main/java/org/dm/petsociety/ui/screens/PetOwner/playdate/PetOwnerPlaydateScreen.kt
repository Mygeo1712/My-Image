// File: org.dm.petsociety.ui.screens.petowner.playdate.PetOwnerPlaydateScreen.kt (REVISI LENGKAP)

package org.dm.petsociety.ui.screens.petowner.playdate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import org.dm.petsociety.model.Pet
import org.dm.petsociety.model.PetType
import org.dm.petsociety.ui.screens.AppBottomBar
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.ChatViewModel
import org.dm.petsociety.viewmodel.PlaydateViewModel
import org.dm.petsociety.viewmodel.PlaydateViewModelFactory
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetOwnerPlaydateScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val playdateViewModel: PlaydateViewModel = viewModel(factory = PlaydateViewModelFactory())
    val chatViewModel: ChatViewModel = viewModel()

    val state by playdateViewModel.state.collectAsState()
    val selectedPet by playdateViewModel.selectedPetDetail.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        // PERUBAHAN KRITIS: Ganti AppTopBar dengan kustom TopAppBar
        topBar = {
            TopAppBar(
                title = { Text("Playdate Hub", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        // NAVIGASI BALIK KE HALAMAN SEARCH
                        navController.popBackStack("home_owner_search", inclusive = false)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali ke Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDarkNavy)
            )
        },
        containerColor = PrimaryDarkNavy,
        bottomBar = { AppBottomBar(navController = navController, currentRoute = "home_owner_search") }, // Menggunakan rute Search agar BottomBar aktif di tab yang benar
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryDarkNavy)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                // --- Filter Tipe Hewan (Chip) ---
                Text(
                    "Temukan teman playdate untuk:",
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PetType.options.forEach { type ->
                        FilterChip(
                            selected = state.filterType == type,
                            onClick = { playdateViewModel.setFilterType(type) },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentTeal,
                                selectedLabelColor = PrimaryDarkNavy,
                                containerColor = Color(0xFF2E3B59),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                // --- Daftar Hewan (Grid) ---
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentTeal)
                    }
                } else if (state.pets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            "Belum ada hewan jenis ${state.filterType} yang tersedia untuk Playdate.",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 60.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.pets, key = { it.petId }) { pet ->
                            PlaydatePetCard(pet = pet) {
                                playdateViewModel.selectPetForDetail(pet) // Tampilkan modal
                            }
                        }
                    }
                }
            }

            // --- Modal Detail (Tindihan di atas konten) ---
            if (selectedPet != null) {
                PlaydateDetailModal(
                    pet = selectedPet!!,
                    onDismiss = { playdateViewModel.selectPetForDetail(null) },
                    onChatClick = { targetPet, ownerName ->
                        if (currentUser != null) {
                            chatViewModel.startChat(
                                currentUid = currentUser.uid,
                                targetUid = targetPet.ownerId, // UID owner pet
                                currentName = authViewModel.authState.value.userName,
                                targetName = ownerName, // Nama Owner Pet
                                navController = navController
                            )
                            playdateViewModel.selectPetForDetail(null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlaydatePetCard(pet: Pet, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B59)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.clickable { onClick() }.height(200.dp)
    ) {
        Column {
            AsyncImage(
                model = pet.profileImageUrl ?: "https://via.placeholder.com/300",
                contentDescription = pet.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(pet.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text("${pet.breed} • ${pet.age} Thn", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PlaydateDetailModal(
    pet: Pet,
    onDismiss: () -> Unit,
    onChatClick: (Pet, String) -> Unit
) {
    val playdateViewModel: PlaydateViewModel = viewModel(factory = PlaydateViewModelFactory())
    val scope = rememberCoroutineScope()
    var ownerName by remember { mutableStateOf("Loading...") }
    val scrollState = rememberScrollState()

    // Ambil Nama Owner
    LaunchedEffect(pet.ownerId) {
        scope.launch {
            ownerName = playdateViewModel.getOwnerName(pet.ownerId)
        }
    }

    // Overlay Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clickable(enabled = false) {}
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                // Gambar Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = pet.profileImageUrl ?: "https://via.placeholder.com/400",
                        contentDescription = pet.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                // Detail Konten
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(pet.name, color = AccentTeal, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text(pet.breed, color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Owner: $ownerName", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Jenis Kelamin: ${pet.gender}", color = Color.LightGray)
                    Text("Usia: ${pet.age} Tahun", color = Color.LightGray)
                    Text("Berat: ${pet.weight} Kg", color = Color.LightGray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Catatan Pemilik:", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(pet.notes ?: "Tidak ada catatan.", color = Color.LightGray, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Chat
                    Button(
                        onClick = { onChatClick(pet, ownerName) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        enabled = ownerName != "Loading..." && ownerName != "Pet Owner"
                    ) {
                        Icon(Icons.Default.Chat, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat $ownerName")
                    }
                }
            }
        }
    }
}