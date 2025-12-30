// File: org.dm.petsociety.ui.screens.petowner.adoption.AdoptionDetailScreen.kt (REVISI LENGKAP)

package org.dm.petsociety.ui.screens.petowner.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Pet
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import org.dm.petsociety.viewmodel.ChatViewModel
import org.dm.petsociety.viewmodel.AuthViewModel // Diperlukan untuk current user name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionDetailScreen(
    navController: NavController,
    petId: String
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val chatViewModel: ChatViewModel = viewModel()

    // Asumsi AuthViewModel tersedia di scope NavHost (seperti di MainActivity)
    val authViewModel: AuthViewModel = viewModel()

    val currentUser = auth.currentUser

    var pet by remember { mutableStateOf<Pet?>(null) }
    var ownerName by remember { mutableStateOf("Loading...") }
    var ownerRole by remember { mutableStateOf("Loading...") } // Ambil Role Owner

    LaunchedEffect(petId) {
        try {
            val doc = db.collection("pets").document(petId).get().await()
            val fetchedPet = doc.toObject(Pet::class.java)
            pet = fetchedPet

            // Ambil nama dan role Owner (Shelter)
            if (fetchedPet != null && fetchedPet.ownerId.isNotEmpty()) {
                val userDoc = db.collection("users").document(fetchedPet.ownerId).get().await()
                ownerName = userDoc.getString("name") ?: "Unknown Shelter"
                ownerRole = userDoc.getString("role") ?: "Shelter/Rescuer"
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    if (pet == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val p = pet!!
        Scaffold(
            bottomBar = {
                // Tombol Hubungi
                Button(
                    onClick = {
                        if (currentUser != null) {
                            // LOGIC CHAT: Pet Owner ke Shelter
                            chatViewModel.startChat(
                                currentUid = currentUser.uid,
                                targetUid = p.ownerId, // Target: Shelter ID
                                currentName = authViewModel.authState.value.userName, // Nama Pet Owner
                                targetName = ownerName, // Nama Shelter
                                navController = navController
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hubungi Pemilik ($ownerName)")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Gambar Utama
                Box {
                    AsyncImage(
                        model = p.profileImageUrl ?: "https://via.placeholder.com/400",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Tombol Back Floating
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                // Konten Detail
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = p.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(text = p.breed, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }
                        Text(
                            text = "${p.age} Tahun",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Diposting oleh: $ownerName ($ownerRole)", color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Cards (Berat, Gender, Tipe)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DetailInfoItem(label = "Berat", value = "${p.weight} Kg", icon = Icons.Default.MonitorWeight)
                        DetailInfoItem(label = "Gender", value = p.gender, icon = Icons.Default.Pets)
                        DetailInfoItem(label = "Tipe", value = p.type, icon = Icons.Default.Pets)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Tentang ${p.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = p.notes ?: "Tidak ada deskripsi tambahan dari shelter.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}