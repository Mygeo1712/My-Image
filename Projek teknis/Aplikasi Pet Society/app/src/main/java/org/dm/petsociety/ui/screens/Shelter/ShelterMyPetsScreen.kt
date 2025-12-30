package org.dm.petsociety.ui.screens.shelter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.dm.petsociety.model.Pet
import org.dm.petsociety.viewmodel.ShelterViewModel

// Definisi Warna Tema (Sesuai file sebelumnya)
val DarkBlueBg = Color(0xFF141927)
val CardDarkBg = Color(0xFF1F2937)

@Composable
fun ShelterMyPetsScreen(
    navController: NavController,
    viewModel: ShelterViewModel
) {
    val myPets by viewModel.myPets.collectAsState()

    // Refresh data setiap kali layar dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchMyPets()
    }

    Scaffold(
        containerColor = DarkBlueBg, // Background Gelap
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("shelter_add_pet") },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        // Column Utama untuk memisahkan Header dan Isi
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // --- HEADER / SAMBUTAN ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Selamat datang, penyelamat hewan!👋",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hewan Asuhan Anda :",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // --- ISI KONTEN (LIST ATAU KOSONG) ---
            if (myPets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f) // Mengisi sisa ruang
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada hewan asuhan.", color = Color.Gray)
                        Text("Klik + untuk mulai menyelamatkan.", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f) // Mengisi sisa ruang agar bisa discroll
                ) {
                    items(myPets) { pet ->
                        PetItemCard(
                            pet = pet,
                            onDeleteClick = { viewModel.deletePet(pet.petId) },
                            onClick = {
                                // Navigasi ke Detail
                                navController.navigate("shelter_pet_detail/${pet.petId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetItemCard(
    pet: Pet,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    // Cek Status untuk Badge
    val isAdopted = pet.status == "Adopted" || pet.status == "Teradopsi"

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkBg), // Kartu Gelap
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Gambar
            AsyncImage(
                model = pet.profileImageUrl ?: "https://via.placeholder.com/150",
                contentDescription = pet.name,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pet.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White // Teks Putih
                    )

                    // Tombol Delete
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF5350))
                    }
                }

                Text(
                    text = "${pet.breed} • ${pet.age} Thn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    // Warna Chip menyesuaikan status (Gelap style)
                    color = if (isAdopted) Color(0xFF3E2723) else Color(0xFF1B5E20),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = if (isAdopted) "Teradopsi" else "Tersedia",
                        color = if (isAdopted) Color(0xFFFFCDD2) else Color(0xFFA5D6A7),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}