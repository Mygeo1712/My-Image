// File: org.dm.petsociety.ui.screens.shelter.AddPetScreen.kt
package org.dm.petsociety.ui.screens.shelter

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dm.petsociety.model.PetType
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.ShelterViewModel
import org.dm.petsociety.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    viewModel: ShelterViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Scope untuk menjalankan proses berat di background
    val coroutineScope = rememberCoroutineScope()

    // Form State
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PetType.options.firstOrNull() ?: "Anjing") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Jantan") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // State Processing Gambar
    var isProcessingImage by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    // Handle Navigasi Balik saat Sukses
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            Toast.makeText(context, (uiState as UiState.Success).message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onBackClick()
        } else if (uiState is UiState.Error) {
            Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Hewan Adopsi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // ... (Bagian UI Upload Foto & Input Text SAMA SEPERTI SEBELUMNYA) ...
            // Agar kode tidak terlalu panjang, saya asumsikan bagian UI Input Field (Nama, Ras, Umur, dll)
            // tetap sama persis dengan kode Anda sebelumnya. Fokus kita ada di Tombol Button di bawah ini.

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Text("Sentuh untuk upload foto", color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields (Copy dari kode sebelumnya atau ketik ulang bagian ini)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Hewan") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            // Radio Button Jenis Hewan
            Row {
                PetType.options.take(2).forEach { type ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = (selectedType == type), onClick = { selectedType = type })
                        Text(text = type, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            }

            OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Ras") }, modifier = Modifier.fillMaxWidth())

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = age, onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                    label = { Text("Umur (Thn)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = weight, onValueChange = { if (it.all { c -> c.isDigit() }) weight = it },
                    label = { Text("Berat (Kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Gender Radio
            Row {
                listOf("Jantan", "Betina").forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = (selectedGender == gender), onClick = { selectedGender = gender })
                        Text(text = gender, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            }

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- PERBAIKAN UTAMA ADA DI SINI (BUTTON) ---
            Button(
                onClick = {
                    if (name.isNotEmpty() && imageUri != null) {
                        isProcessingImage = true // Tampilkan loading lokal

                        // JALANKAN DI BACKGROUND THREAD (IO)
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                // Proses berat konversi gambar
                                val bitmap = uriToBitmap(context, imageUri!!)

                                // Kembali ke Main Thread untuk update ViewModel
                                withContext(Dispatchers.Main) {
                                    isProcessingImage = false
                                    if (bitmap != null) {
                                        viewModel.addShelterPet(
                                            name = name,
                                            type = selectedType,
                                            breed = breed,
                                            age = age,
                                            weight = weight,
                                            gender = selectedGender,
                                            notes = description,
                                            imageBitmap = bitmap
                                        )
                                    } else {
                                        Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isProcessingImage = false
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Nama dan Foto wajib diisi", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState !is UiState.Loading && !isProcessingImage, // Disable saat loading
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                if (uiState is UiState.Loading || isProcessingImage) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isProcessingImage) "Memproses Gambar..." else "Mengupload...")
                } else {
                    Text("Terbitkan Adopsi")
                }
            }
        }
    }
}