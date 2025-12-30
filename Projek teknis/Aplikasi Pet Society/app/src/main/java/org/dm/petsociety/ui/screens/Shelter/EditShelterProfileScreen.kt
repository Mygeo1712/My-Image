package org.dm.petsociety.ui.screens.shelter

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.ShelterViewModel
import org.dm.petsociety.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShelterProfileScreen(
    navController: NavController,
    viewModel: ShelterViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.shelterProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf(profile.name) }
    var description by remember { mutableStateOf(profile.description) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Update state local saat data profil ter-load
    LaunchedEffect(profile) {
        if (name.isEmpty()) name = profile.name
        if (description.isEmpty()) description = profile.description
    }

    // Handle Loading & Success
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            Toast.makeText(context, (uiState as UiState.Success).message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Edit Foto
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (profile.photoUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.photoUrl),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                    }
                }
            }
            Text("Ganti Foto", color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Shelter / Rescuer") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Profil") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val bitmap = if (imageUri != null) uriToBitmap(context, imageUri!!) else null
                    viewModel.updateProfile(name, description, bitmap)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState !is UiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}