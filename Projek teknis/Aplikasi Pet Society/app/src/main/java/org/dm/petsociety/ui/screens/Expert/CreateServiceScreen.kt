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
import androidx.compose.material.icons.filled.*
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
import org.dm.petsociety.model.ExpertService
import org.dm.petsociety.model.ServiceCategory
import org.dm.petsociety.network.CloudinaryClient
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.ExpertViewModel
import org.dm.petsociety.viewmodel.UiState
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
    navController: androidx.navigation.NavController,
    viewModel: ExpertViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State Form
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Medis") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Layanan Baru") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF141927), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF141927)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {

            // Upload Foto
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFF1F2937), RoundedCornerShape(8.dp)).clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) Image(painter = rememberAsyncImagePainter(imageUri), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.CameraAlt, null, tint = Color.Gray); Text("Upload Foto", color = Color.Gray) }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1F2937), unfocusedContainerColor = Color(0xFF1F2937),
                focusedBorderColor = Color(0xFF4CAF50), unfocusedBorderColor = Color.Gray
            )

            // Nama Servis
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Nama Servis") }, modifier = Modifier.fillMaxWidth(), colors = colors)
            Spacer(modifier = Modifier.height(16.dp))

            // Kategori Dropdown
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category, onValueChange = {}, readOnly = true, label = { Text("Kategori") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                    colors = colors, modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    ServiceCategory.options.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { category = option; isDropdownExpanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Harga
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Estimasi Harga") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, prefix = { Text("Rp ", color = Color.White) })
            Spacer(modifier = Modifier.height(16.dp))

            // Lokasi
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth(), colors = colors, leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50)) })
            Spacer(modifier = Modifier.height(16.dp))

            // Deskripsi
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth().height(100.dp), colors = colors)
            Spacer(modifier = Modifier.height(24.dp))

            // Logic Simpan Manual (Karena ViewModel addService belum support field 'Location')
            // Kita inject logic manual disini agar cepat tanpa ubah ViewModel yang rumit
            Button(
                onClick = {
                    if (title.isNotEmpty() && imageUri != null) {
                        isLoading = true
                        scope.launch(Dispatchers.IO) {
                            try {
                                val bitmap = uriToBitmap(context, imageUri!!)
                                val url = if (bitmap != null) CloudinaryClient.uploadImage(bitmap) else ""
                                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val newId = UUID.randomUUID().toString()
                                val service = ExpertService(newId, uid, title, category, description, price, location, url)
                                FirebaseFirestore.getInstance().collection("expert_services").document(newId).set(service).await()

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    Toast.makeText(context, "Berhasil!", Toast.LENGTH_SHORT).show()
                                    viewModel.fetchMyServices() // Refresh data
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { isLoading = false; Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show() }
                            }
                        }
                    } else Toast.makeText(context, "Data belum lengkap!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text("Posting Layanan")
            }
        }
    }
}