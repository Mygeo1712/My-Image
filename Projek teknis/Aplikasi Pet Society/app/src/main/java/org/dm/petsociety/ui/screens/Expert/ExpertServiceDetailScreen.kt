// File: org.dm.petsociety.ui.screens.expert.ExpertServiceDetailScreen.kt (REVISI LENGKAP)

package org.dm.petsociety.ui.screens.expert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dm.petsociety.model.ExpertService
import org.dm.petsociety.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dm.petsociety.viewmodel.AuthViewModel // Diperlukan untuk current user name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertServiceDetailScreen(
    navController: NavController,
    serviceId: String
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    // Inisialisasi ViewModel
    val chatViewModel: ChatViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel() // Asumsi AuthVM tersedia di scope NavHost
    val currentUser = auth.currentUser

    var service by remember { mutableStateOf<ExpertService?>(null) }
    var expertName by remember { mutableStateOf("") }
    var expertRole by remember { mutableStateOf("") }

    LaunchedEffect(serviceId) {
        val doc = db.collection("expert_services").document(serviceId).get().await()
        val s = doc.toObject(ExpertService::class.java)
        service = s
        if (s != null) {
            val userDoc = db.collection("users").document(s.expertId).get().await()
            expertName = userDoc.getString("name") ?: "Expert"
            expertRole = userDoc.getString("role") ?: "Expert/Business"
        }
    }

    if (service == null) {
        Box(Modifier.fillMaxSize().background(Color(0xFF141927)), Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        val s = service!!
        val isOwner = s.expertId == auth.currentUser?.uid
        var currentStatus by remember { mutableStateOf(s.status) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail Layanan", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141927))
                )
            },
            containerColor = Color(0xFF141927)
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = s.imageUrl.ifEmpty { "https://via.placeholder.com/400" },
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.serviceName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Surface(
                        color = Color(0xFF1F2937),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(s.category, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }

                    // --- STATUS LAYANAN (OWNER ONLY) ---
                    if (isOwner) {
                        // ... (LOGIKA STATUS OWNER TETAP SAMA) ...
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Status Ketersediaan", color = Color.White, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = currentStatus == "Available",
                                        onClick = {
                                            currentStatus = "Available"
                                            scope.launch { db.collection("expert_services").document(serviceId).update("status", "Available").await() }
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color.Green, unselectedColor = Color.Gray)
                                    )
                                    Text("Tersedia", color = Color.Green)

                                    Spacer(modifier = Modifier.width(16.dp))

                                    RadioButton(
                                        selected = currentStatus == "Unavailable",
                                        onClick = {
                                            currentStatus = "Unavailable"
                                            scope.launch { db.collection("expert_services").document(serviceId).update("status", "Unavailable").await() }
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color.Red, unselectedColor = Color.Gray)
                                    )
                                    Text("Tidak Tersedia", color = Color.Red)
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if(currentStatus == "Available") "• Tersedia" else "• Tidak Tersedia",
                            color = if(currentStatus == "Available") Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(s.price, color = Color(0xFF4CAF50), fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    if (s.location.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(s.location, color = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Tentang Layanan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(s.description, color = Color.LightGray, lineHeight = 22.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- TOMBOL AKSI (Pet Owner klik Chat Expert) ---
                    if (!isOwner) {
                        Button(
                            onClick = {
                                // LOGIC CHAT BARU: Pet Owner ke Expert/Business
                                if (currentUser != null) {
                                    chatViewModel.startChat(
                                        currentUid = currentUser.uid,
                                        targetUid = s.expertId, // Expert ID sebagai target
                                        currentName = authViewModel.authState.value.userName, // Nama Pet Owner
                                        targetName = expertName, // Nama Expert
                                        navController = navController
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Icon(Icons.Default.Chat, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat Expert")
                        }
                    } else {
                        // ... (LOGIKA TOMBOL EDIT OWNER TETAP SAMA) ...
                        OutlinedButton(
                            onClick = { navController.navigate("expert_edit_service/${s.serviceId}") },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Layanan Ini")
                        }
                    }
                }
            }
        }
    }
}