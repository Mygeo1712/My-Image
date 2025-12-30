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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.dm.petsociety.model.Pet
import org.dm.petsociety.model.PetType
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.ShelterViewModel
import org.dm.petsociety.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen(
    navController: androidx.navigation.NavController,
    viewModel: ShelterViewModel,
    petId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    // State Form
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Jantan") }
    var description by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoadingData by remember { mutableStateOf(true) }
    var isProcessingImage by remember { mutableStateOf(false) }

    // Fetch Data Awal
    LaunchedEffect(petId) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("pets").document(petId).get().await()
            val pet = doc.toObject(Pet::class.java)
            if (pet != null) {
                name = pet.name
                selectedType = pet.type
                breed = pet.breed
                age = pet.age.toString()
                weight = pet.weight.toString()
                selectedGender = pet.gender
                description = pet.notes ?: ""
                currentImageUrl = pet.profileImageUrl
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoadingData = false
        }
    }

    // Handle Hasil Simpan
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            Toast.makeText(context, (uiState as UiState.Success).message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Info Hewan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141927), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF141927) // Background Biru Gelap
    ) { padding ->
        if (isLoadingData) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Upload Foto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF1F2937), RoundedCornerShape(8.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(painter = rememberAsyncImagePainter(imageUri), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else if (currentImageUrl != null) {
                        Image(painter = rememberAsyncImagePainter(currentImageUrl), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
                            Text("Ganti Foto", color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Style untuk Text Field Gelap
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1F2937),
                    unfocusedContainerColor = Color(0xFF1F2937),
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF4CAF50),
                    unfocusedLabelColor = Color.Gray
                )

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
                Spacer(modifier = Modifier.height(8.dp))

                // Tipe Hewan
                Text("Jenis Hewan:", color = Color.White)
                Row {
                    PetType.options.take(2).forEach { type ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (selectedType == type), onClick = { selectedType = type }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50), unselectedColor = Color.Gray))
                            Text(text = type, color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }

                OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Ras") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = age, onValueChange = { if (it.all { c -> c.isDigit() }) age = it }, label = { Text("Umur") }, modifier = Modifier.weight(1f).padding(end = 4.dp), colors = textFieldColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = weight, onValueChange = { if (it.all { c -> c.isDigit() }) weight = it }, label = { Text("Berat") }, modifier = Modifier.weight(1f).padding(start = 4.dp), colors = textFieldColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Gender:", color = Color.White)
                Row {
                    listOf("Jantan", "Betina").forEach { gender ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (selectedGender == gender), onClick = { selectedGender = gender }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50), unselectedColor = Color.Gray))
                            Text(text = gender, color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 5, colors = textFieldColors)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isProcessingImage = true
                        scope.launch(Dispatchers.IO) {
                            val bitmap = if (imageUri != null) uriToBitmap(context, imageUri!!) else null
                            withContext(Dispatchers.Main) {
                                isProcessingImage = false
                                viewModel.updateShelterPet(
                                    petId = petId, name = name, type = selectedType, breed = breed,
                                    age = age, weight = weight, gender = selectedGender, notes = description,
                                    imageBitmap = bitmap, currentImageUrl = currentImageUrl
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isProcessingImage && uiState !is UiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    if (isProcessingImage || uiState is UiState.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Simpan Perubahan", color = Color.White)
                }
            }
        }
    }
}