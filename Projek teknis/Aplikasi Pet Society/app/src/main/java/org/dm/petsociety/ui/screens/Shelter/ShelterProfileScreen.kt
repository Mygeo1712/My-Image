package org.dm.petsociety.ui.screens.shelter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.dm.petsociety.model.Pet
import org.dm.petsociety.viewmodel.ShelterViewModel

// Warna Tema
val DarkBlueBgProfile = Color(0xFF141927)
val CardDarkBgProfile = Color(0xFF1F2937)

@Composable
fun ShelterProfileScreen(navController: NavController, viewModel: ShelterViewModel) {
    val profile by viewModel.shelterProfile.collectAsState()
    val myPets by viewModel.myPets.collectAsState()

    // State untuk Dialog Konfirmasi Hapus
    var showDeleteDialog by remember { mutableStateOf(false) }
    var petToDelete by remember { mutableStateOf<Pet?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
        viewModel.fetchMyPets()
    }

    // --- DIALOG KONFIRMASI HAPUS ---
    if (showDeleteDialog && petToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Postingan?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus ${petToDelete?.name}? Data yang dihapus tidak dapat dikembalikan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        petToDelete?.let { viewModel.deletePet(it.petId) }
                        showDeleteDialog = false
                        petToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF5350))
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.DarkGray
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBgProfile)
    ) {
        // --- HEADER PROFIL ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDarkBgProfile)
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
            Text(text = "Shelter/Rescuer", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = profile.description, color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { navController.navigate("shelter_edit_profile") },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profil")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Hewan Siap Adopsi (${myPets.size})", modifier = Modifier.padding(start = 16.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)

        if (myPets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada hewan yang diupload.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myPets) { pet ->
                    ShelterPetGridItem(
                        pet = pet,
                        onClick = { navController.navigate("shelter_pet_detail/${pet.petId}") },
                        onDeleteClick = {
                            // Munculkan Dialog Konfirmasi
                            petToDelete = pet
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShelterPetGridItem(
    pet: Pet,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit // Parameter Baru
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkBgProfile),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        // Gunakan Box agar icon delete bisa ditaruh di atas gambar (Pojok Kanan Atas)
        Box {
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
                    if (pet.status == "Adopted" || pet.status == "Teradopsi") {
                        Text(text = "• Teradopsi", fontSize = 10.sp, color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- TOMBOL HAPUS (POJOK KANAN ATAS) ---
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape) // Background transparan gelap agar terlihat
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color(0xFFEF5350), // Merah
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}