package org.dm.petsociety.ui.screens.shelter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.Pet
import org.dm.petsociety.viewmodel.ChatViewModel // Import ChatViewModel

// Warna Tema
private val DetailBg = Color(0xFF141927)
private val DetailCardBg = Color(0xFF1F2937)
private val TextWhite = Color.White
private val TextGray = Color.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterPetDetailScreen(
    navController: NavController,
    petId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Inisialisasi ChatViewModel
    val chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val currentUser = auth.currentUser

    var pet by remember { mutableStateOf<Pet?>(null) }
    var ownerName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Available") }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch Data
    LaunchedEffect(petId) {
        try {
            val doc = db.collection("pets").document(petId).get().await()
            val fetchedPet = doc.toObject(Pet::class.java)
            if (fetchedPet != null) {
                pet = fetchedPet
                status = fetchedPet.status

                // Ambil Nama Pemosting
                val userDoc = db.collection("users").document(fetchedPet.ownerId).get().await()
                ownerName = userDoc.getString("name") ?: "Rescuer"
            }
        } catch (e: Exception) { }
    }

    if (pet == null) {
        Box(modifier = Modifier.fillMaxSize().background(DetailBg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TextWhite)
        }
    } else {
        val p = pet!!
        val isMyPet = p.ownerId == currentUser?.uid // Cek kepemilikan

        Scaffold(
            containerColor = DetailBg,
            bottomBar = {
                Column(modifier = Modifier.background(DetailCardBg)) {
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isMyPet) {
                            // --- TAMPILAN JIKA MILIK SENDIRI (EDIT) ---
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        try {
                                            db.collection("pets").document(petId).update("status", status).await()
                                            Toast.makeText(context, "Status disimpan!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } catch (e: Exception) { } finally { isLoading = false }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Text(if (isLoading) "Loading..." else "Simpan Status")
                            }

                            OutlinedButton(
                                onClick = { navController.navigate("shelter_edit_pet/$petId") },
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Info")
                            }
                        } else {
                            // --- TAMPILAN JIKA MILIK ORANG LAIN (HUBUNGI) ---
                            Button(
                                onClick = {
                                    // LOGIC CHAT UNIVERSAL
                                    if (currentUser != null) {
                                        chatViewModel.startChat(
                                            currentUid = currentUser.uid,
                                            targetUid = p.ownerId, // Shelter ID
                                            currentName = currentUser.displayName ?: "Saya",
                                            targetName = ownerName, // Nama Shelter
                                            navController = navController
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hubungi $ownerName")
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                // Gambar
                Box {
                    AsyncImage(
                        model = p.profileImageUrl ?: "https://via.placeholder.com/400",
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                // Info Detail
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = p.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(text = p.breed, style = MaterialTheme.typography.bodyLarge, color = TextGray)
                            // Info Pemosting
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Diposting oleh: $ownerName", color = Color(0xFF81C784), fontSize = 14.sp)
                            }
                        }
                        Text(
                            text = "${p.age} Tahun",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Jika milik sendiri, tampilkan pilihan status
                    if (isMyPet) {
                        Card(colors = CardDefaults.cardColors(containerColor = DetailCardBg), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Status Adopsi", fontWeight = FontWeight.Bold, color = TextWhite)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = status == "Available", onClick = { status = "Available" }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50), unselectedColor = TextGray))
                                    Text("Tersedia", color = TextWhite)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    RadioButton(selected = status == "Adopted" || status == "Teradopsi", onClick = { status = "Adopted" }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFEF5350), unselectedColor = TextGray))
                                    Text("Telah Diadopsi", color = TextWhite)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Grid Info
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DetailInfoItem(label = "Berat", value = "${p.weight} Kg", icon = Icons.Default.MonitorWeight)
                        DetailInfoItem(label = "Gender", value = p.gender, icon = Icons.Default.Pets)
                        DetailInfoItem(label = "Tipe", value = p.type, icon = Icons.Default.Pets)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Tentang ${p.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = p.notes ?: "Tidak ada deskripsi.", style = MaterialTheme.typography.bodyMedium, lineHeight = 24.sp, color = TextGray)
                }
            }
        }
    }
}

// FUNGSI INI YANG SEBELUMNYA HILANG
@Composable
fun DetailInfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(DetailCardBg, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, color = TextWhite)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextGray)
    }
}