package org.dm.petsociety.ui.screens.expert

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.ExpertViewModel
import org.dm.petsociety.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpertProfileScreen(
    navController: androidx.navigation.NavController,
    viewModel: ExpertViewModel
) {
    val context = LocalContext.current
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf(userProfile.name) }
    var bio by remember { mutableStateOf(userProfile.bio) }
    var location by remember { mutableStateOf(userProfile.location) }
    var specialization by remember { mutableStateOf(userProfile.specialization) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Update state local saat data profil ter-load
    LaunchedEffect(userProfile) {
        if (name.isEmpty()) name = userProfile.name
        if (bio.isEmpty()) bio = userProfile.bio
        if (location.isEmpty()) location = userProfile.location
        if (specialization.isEmpty()) specialization = userProfile.specialization
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            Toast.makeText(context, (uiState as UiState.Success).message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil Expert") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141927), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF141927)
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Edit Foto
            Box(
                modifier = Modifier.size(120.dp).clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) Image(painter = rememberAsyncImagePainter(imageUri), null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                else if (userProfile.profileImageUrl.isNotEmpty()) Image(painter = rememberAsyncImagePainter(userProfile.profileImageUrl), null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                else Box(Modifier.fillMaxSize().clip(CircleShape).background(Color.Gray), Alignment.Center) { Icon(Icons.Default.CameraAlt, null, tint = Color.White) }
            }
            Text("Ganti Foto", color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            val colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1F2937), unfocusedContainerColor = Color(0xFF1F2937),
                focusedBorderColor = Color(0xFF4CAF50), unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF4CAF50), unfocusedLabelColor = Color.Gray
            )

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), colors = colors)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = specialization, onValueChange = { specialization = it }, label = { Text("Spesialisasi (Contoh: Dokter Hewan)") }, modifier = Modifier.fillMaxWidth(), colors = colors)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lokasi Praktik") }, modifier = Modifier.fillMaxWidth(), colors = colors)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio / Deskripsi Singkat") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 5, colors = colors)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val bitmap = if (imageUri != null) uriToBitmap(context, imageUri!!) else null
                    viewModel.updateExpertProfile(name, bio, location, specialization, bitmap)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = uiState !is UiState.Loading
            ) {
                if (uiState is UiState.Loading) CircularProgressIndicator(color = Color.White) else Text("Simpan Profil")
            }
        }
    }
}