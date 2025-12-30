// File: org.dm.petsociety.ui.screens.petowner.profile.PetListScreen.kt (REVISI LENGKAP)

package org.dm.petsociety.ui.screens.petowner.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.dm.petsociety.model.Pet
import org.dm.petsociety.model.PetType
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.util.createTempUri
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.ProfileViewModel
import org.dm.petsociety.viewmodel.ProfileViewModelFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import coil.compose.AsyncImage // Import untuk menampilkan gambar dari URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(authViewModel))
    val state by viewModel.state.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // State untuk Long Press Delete Pet
    var showDeletePetDialog by remember { mutableStateOf(false) }
    var petToDelete by remember { mutableStateOf<Pet?>(null) }

    // State untuk Edit Pet
    var showEditDialog by remember { mutableStateOf(false) }
    var petToEdit by remember { mutableStateOf<Pet?>(null) }


    // --- DIALOG KONFIRMASI HAPUS PET ---
    if (showDeletePetDialog && petToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeletePetDialog = false; petToDelete = null },
            title = { Text("Hapus Peliharaan?", color = PrimaryDarkNavy, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus ${petToDelete?.name} dari daftar peliharaan Anda?", color = PrimaryDarkNavy) },
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

    // --- DIALOG EDIT PET ---
    if (showEditDialog && petToEdit != null) {
        EditPetDialog(
            pet = petToEdit!!,
            onDismiss = { showEditDialog = false; petToEdit = null },
            onUpdate = { updatedPet, bitmap ->
                // Panggil fungsi update di ProfileViewModel
                viewModel.updatePet(updatedPet, bitmap)
                showEditDialog = false
                petToEdit = null
            }
        )
    }


    Scaffold(
        topBar = {
            AppTopBar(title = "Daftar Peliharaan", navController = navController, showProfile = false)
        },
        containerColor = PrimaryDarkNavy,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentTeal,
                contentColor = PrimaryDarkNavy
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Hewan")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryDarkNavy)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Kelola Hewan Peliharaan Anda",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "<< Kembali ke Profil",
                        color = AccentTeal,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
                Text("Total: (${state.pets.size})", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 16.dp))
            }

            items(state.pets, key = { it.petId }) { pet ->
                PetListItem(
                    pet = pet,
                    onEditClick = {
                        petToEdit = it
                        showEditDialog = true
                    },
                    onLongClick = {
                        petToDelete = it
                        showDeletePetDialog = true
                    }
                )
            }

            item {
                if (state.pets.isEmpty() && !state.isLoading) {
                    Text(
                        "Belum ada hewan yang terdaftar. Klik + untuk menambah.",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPetDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { pet, bitmap ->
                viewModel.addPet(pet, bitmap)
                showAddDialog = false
            }
        )
    }
}

// --- Komponen Pet List Item yang modern dan interaktif ---
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PetListItem(pet: Pet, onEditClick: (Pet) -> Unit, onLongClick: (Pet) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = { /* Tidak ada aksi klik tunggal di sini, fokus ke long click */ },
                onLongClick = { onLongClick(pet) } // Trigger delete dialog
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E283A)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Ketinggian tetap untuk konsistensi
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Gambar/Avatar Pet
            AsyncImage(
                model = pet.profileImageUrl ?: "https://via.placeholder.com/50",
                contentDescription = pet.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AccentTeal.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Info Pet
            Column(modifier = Modifier.weight(1f)) {
                Text(pet.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, maxLines = 1)
                Text(
                    "${pet.breed} | ${pet.age} Thn | ${pet.gender}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            // 3. Aksi (Edit & Delete Icon)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tombol Edit
                IconButton(onClick = { onEditClick(pet) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AccentTeal)
                }
                // Indikator Long Press (Delete)
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}


// --- Komponen Dialog Tambah Pet (Tetap Sama dari Revisi sebelumnya) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetDialog(onDismiss: () -> Unit, onAdd: (Pet, Bitmap?) -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PetType.options.first()) }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Jantan") }
    var notes by remember { mutableStateOf("") }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    var isTypeDropdownExpanded by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && breed.isNotBlank() && age.toIntOrNull() != null && selectedBitmap != null

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(), onResult = { success ->
            val currentTempUri = tempImageUri
            if (success && currentTempUri != null) { selectedBitmap = uriToBitmap(context, currentTempUri) }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = { uri: Uri? ->
            if (uri != null) { selectedBitmap = uriToBitmap(context, uri) }
        }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val newUri = createTempUri(context)
            tempImageUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan.", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daftarkan Peliharaan Baru", color = PrimaryDarkNavy, fontWeight = FontWeight.Bold) },
        containerColor = Color.White,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 1. FOTO PROFIL ---
                Text("Foto Profil Hewan:", fontWeight = FontWeight.SemiBold, color = PrimaryDarkNavy)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Preview Gambar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEEEEE))
                            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedBitmap != null) {
                            Image(bitmap = selectedBitmap!!.asImageBitmap(), contentDescription = "Foto Peliharaan", modifier = Modifier.fillMaxSize())
                        } else {
                            Icon(Icons.Default.Image, null, tint = Color.Gray, modifier = Modifier.size(30.dp))
                        }
                    }

                    // Tombol Upload
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { galleryLauncher.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Galeri", fontSize = 12.sp) }
                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    val newUri = createTempUri(context)
                                    tempImageUri = newUri
                                    cameraLauncher.launch(newUri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy), contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("Kamera", fontSize = 12.sp) }
                    }
                }

                Divider()

                // --- 2. INPUT DATA ---
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Hewan") }, modifier = Modifier.fillMaxWidth())

                // TIPE HEWAN (Dropdown)
                Box {
                    OutlinedTextField(
                        value = type, onValueChange = { }, readOnly = true,
                        label = { Text("Jenis Hewan") },
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "dropdown", Modifier.clickable { isTypeDropdownExpanded = true }) },
                        modifier = Modifier.fillMaxWidth().clickable { isTypeDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isTypeDropdownExpanded,
                        onDismissRequest = { isTypeDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        PetType.options.forEach { item -> DropdownMenuItem(text = { Text(item) }, onClick = { type = item; isTypeDropdownExpanded = false }) }
                    }
                }

                OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Ras") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age, onValueChange = { age = it.filter { c -> c.isDigit() } }, label = { Text("Usia (Thn)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight, onValueChange = { weight = it.filter { c -> c.isDigit() } }, label = { Text("Berat (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                    )
                }

                // GENDER SELECTION
                Text("Kelamin:", color = PrimaryDarkNavy, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { gender = "Jantan" }, colors = ButtonDefaults.buttonColors(containerColor = if (gender == "Jantan") AccentTeal else Color.LightGray)) { Text("Jantan", color = if (gender == "Jantan") PrimaryDarkNavy else PrimaryDarkNavy) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { gender = "Betina" }, colors = ButtonDefaults.buttonColors(containerColor = if (gender == "Betina") AccentTeal else Color.LightGray)) { Text("Betina", color = if (gender == "Betina") PrimaryDarkNavy else PrimaryDarkNavy) }
                }

                // Notes/Deskripsi
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan/Deskripsi (Opsional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pet = Pet(
                        name = name, type = type, breed = breed,
                        age = age.toIntOrNull() ?: 0,
                        weight = weight.toIntOrNull() ?: 0,
                        gender = gender,
                        notes = notes
                    )
                    onAdd(pet, selectedBitmap)
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Batal", color = PrimaryDarkNavy)
            }
        }
    )
}

// --- Komponen Dialog Edit Pet (BARU) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetDialog(pet: Pet, onDismiss: () -> Unit, onUpdate: (Pet, Bitmap?) -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State dari Pet yang akan di-edit
    var name by remember { mutableStateOf(pet.name) }
    var type by remember { mutableStateOf(pet.type) }
    var breed by remember { mutableStateOf(pet.breed) }
    var age by remember { mutableStateOf(pet.age.toString()) }
    var weight by remember { mutableStateOf(pet.weight.toString()) }
    var gender by remember { mutableStateOf(pet.gender) }
    var notes by remember { mutableStateOf(pet.notes ?: "") }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    var isTypeDropdownExpanded by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && breed.isNotBlank() && age.toIntOrNull() != null
    // Catatan: Edit tidak mewajibkan upload foto baru, jadi tidak perlu cek selectedBitmap

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(), onResult = { success ->
            val currentTempUri = tempImageUri
            if (success && currentTempUri != null) { selectedBitmap = uriToBitmap(context, currentTempUri) }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = { uri: Uri? ->
            if (uri != null) { selectedBitmap = uriToBitmap(context, uri) }
        }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val newUri = createTempUri(context)
            tempImageUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan.", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Peliharaan: ${pet.name}", color = PrimaryDarkNavy, fontWeight = FontWeight.Bold) },
        containerColor = Color.White,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 1. FOTO PROFIL ---
                Text("Foto Profil Hewan:", fontWeight = FontWeight.SemiBold, color = PrimaryDarkNavy)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Preview Gambar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEEEEE))
                            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedBitmap != null) {
                            Image(bitmap = selectedBitmap!!.asImageBitmap(), contentDescription = "Foto Peliharaan Baru", modifier = Modifier.fillMaxSize())
                        } else if (pet.profileImageUrl != null) {
                            AsyncImage(model = pet.profileImageUrl, contentDescription = "Foto Peliharaan Lama", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Icon(Icons.Default.Image, null, tint = Color.Gray, modifier = Modifier.size(30.dp))
                        }
                    }

                    // Tombol Upload
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { galleryLauncher.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Galeri", fontSize = 12.sp) }
                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    val newUri = createTempUri(context)
                                    tempImageUri = newUri
                                    cameraLauncher.launch(newUri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy), contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("Kamera", fontSize = 12.sp) }
                    }
                }

                Divider()

                // --- 2. INPUT DATA ---
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Hewan") }, modifier = Modifier.fillMaxWidth())

                // TIPE HEWAN (Dropdown)
                Box {
                    OutlinedTextField(
                        value = type, onValueChange = { }, readOnly = true,
                        label = { Text("Jenis Hewan") },
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "dropdown", Modifier.clickable { isTypeDropdownExpanded = true }) },
                        modifier = Modifier.fillMaxWidth().clickable { isTypeDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isTypeDropdownExpanded,
                        onDismissRequest = { isTypeDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        PetType.options.forEach { item -> DropdownMenuItem(text = { Text(item) }, onClick = { type = item; isTypeDropdownExpanded = false }) }
                    }
                }

                OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Ras") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age, onValueChange = { age = it.filter { c -> c.isDigit() } }, label = { Text("Usia (Thn)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight, onValueChange = { weight = it.filter { c -> c.isDigit() } }, label = { Text("Berat (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                    )
                }

                // GENDER SELECTION
                Text("Kelamin:", color = PrimaryDarkNavy, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { gender = "Jantan" }, colors = ButtonDefaults.buttonColors(containerColor = if (gender == "Jantan") AccentTeal else Color.LightGray)) { Text("Jantan", color = if (gender == "Jantan") PrimaryDarkNavy else PrimaryDarkNavy) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { gender = "Betina" }, colors = ButtonDefaults.buttonColors(containerColor = if (gender == "Betina") AccentTeal else Color.LightGray)) { Text("Betina", color = if (gender == "Betina") PrimaryDarkNavy else PrimaryDarkNavy) }
                }

                // Notes/Deskripsi
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan/Deskripsi (Opsional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedPet = Pet(
                        petId = pet.petId, // ID wajib dipertahankan
                        ownerId = pet.ownerId, // Owner ID wajib dipertahankan
                        name = name, type = type, breed = breed,
                        age = age.toIntOrNull() ?: 0,
                        weight = weight.toIntOrNull() ?: 0,
                        gender = gender,
                        notes = notes,
                        profileImageUrl = pet.profileImageUrl // URL lama dipertahankan jika tidak ada bitmap baru
                    )
                    onUpdate(updatedPet, selectedBitmap)
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
            ) {
                Text("Simpan Perubahan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Batal", color = PrimaryDarkNavy)
            }
        }
    )
}