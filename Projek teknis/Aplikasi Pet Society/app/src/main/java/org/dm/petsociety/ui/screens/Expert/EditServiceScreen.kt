package org.dm.petsociety.ui.screens.expert

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
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
import org.dm.petsociety.model.ExpertService
import org.dm.petsociety.model.ServiceCategory
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.ExpertViewModel
import org.dm.petsociety.viewmodel.UiState
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditServiceScreen(
    navController: androidx.navigation.NavController,
    viewModel: ExpertViewModel,
    serviceId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    // State Form
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoadingData by remember { mutableStateOf(true) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Fetch Data Awal
    LaunchedEffect(serviceId) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("expert_services").document(serviceId).get().await()
            val service = doc.toObject(ExpertService::class.java)
            if (service != null) {
                name = service.serviceName
                category = service.category
                price = service.price
                description = service.description
                location = service.location
                currentImageUrl = service.imageUrl
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoadingData = false
        }
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
                title = { Text("Edit Layanan") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141927), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF141927)
    ) { padding ->
        if (isLoadingData) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) }
        } else {
            Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(scrollState)) {

                // --- Upload Foto ---
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                        .background(Color(0xFF1F2937), RoundedCornerShape(8.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) Image(painter = rememberAsyncImagePainter(imageUri), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else if (currentImageUrl.isNotEmpty()) Image(painter = rememberAsyncImagePainter(currentImageUrl), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.CameraAlt, null, tint = Color.Gray); Text("Ganti Foto", color = Color.Gray) }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Form Inputs ---
                val colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1F2937), unfocusedContainerColor = Color(0xFF1F2937),
                    focusedBorderColor = Color(0xFF4CAF50), unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF4CAF50), unfocusedLabelColor = Color.Gray
                )

                // 1. Nama Servis
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Servis") }, modifier = Modifier.fillMaxWidth(), colors = colors)
                Spacer(modifier = Modifier.height(16.dp))

                // 2. Kategori (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Layanan") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                        colors = colors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        ServiceCategory.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { category = option; isDropdownExpanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 3. Estimasi Harga
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Estimasi Harga") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = colors,
                    prefix = { Text("Rp ", color = Color.White) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 4. Lokasi
                OutlinedTextField(
                    value = location, onValueChange = { location = it },
                    label = { Text("Lokasi (Ketik Sendiri)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = colors,
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50)) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 5. Deskripsi
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Deskripsi Lengkap") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = colors,
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Tombol Simpan
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val bitmap = if (imageUri != null) uriToBitmap(context, imageUri!!) else null
                            withContext(Dispatchers.Main) {
                                viewModel.updateService(serviceId, name, category, price, description, location, bitmap, currentImageUrl)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = uiState !is UiState.Loading
                ) {
                    if (uiState is UiState.Loading) CircularProgressIndicator(color = Color.White)
                    else Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}