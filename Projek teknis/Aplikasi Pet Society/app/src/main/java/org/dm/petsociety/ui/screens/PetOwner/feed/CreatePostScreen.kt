package org.dm.petsociety.ui.screens.petowner.feed

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // <-- IMPORT KRITIS DITAMBAHKAN
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.util.uriToBitmap
import org.dm.petsociety.viewmodel.PostViewModel
import org.dm.petsociety.viewmodel.UploadState
import org.dm.petsociety.viewmodel.PostViewModelFactory
import org.dm.petsociety.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // PENTING: Pastikan PostViewModel tersedia.
    val postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(authViewModel))

    val uploadState by postViewModel.uploadState.collectAsState()

    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Kategori selalu "Umum" untuk Postingan Feed
    val selectedCategory by remember { mutableStateOf("Umum") }

    // DEKLARASI KRITIS YANG HILANG DITAMBAHKAN
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val scope = rememberCoroutineScope()

    // Handle State Upload
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadState.Success -> {
                // MENGGUNAKAN CONTEXT YANG SUDAH DIDEKLARASIKAN
                Toast.makeText(context, "Berhasil diunggah ke Feed!", Toast.LENGTH_SHORT).show()
                postViewModel.clearState()
                navController.popBackStack()
            }
            is UploadState.Error -> {
                // MENGGUNAKAN CONTEXT YANG SUDAH DIDEKLARASIKAN
                Toast.makeText(context, (uploadState as UploadState.Error).message, Toast.LENGTH_SHORT).show()
                postViewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Postingan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDarkNavy)
            )
        },
        containerColor = PrimaryDarkNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Input Text
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Apa yang sedang hewanmu lakukan?", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2E3B59),
                    unfocusedContainerColor = Color(0xFF2E3B59),
                    focusedBorderColor = AccentTeal,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pilihan Kategori Dihapus. Sekarang hanya label Kategori: Umum
            Text("Kategori: Umum (Feed)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Image Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF2E3B59), RoundedCornerShape(12.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Text("Tambah Foto", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Upload
            Button(
                onClick = {
                    if (text.isNotBlank() || imageUri != null) {
                        scope.launch(Dispatchers.IO) {
                            // MENGGUNAKAN CONTEXT YANG SUDAH DIDEKLARASIKAN
                            val bitmap = if (imageUri != null) uriToBitmap(context, imageUri!!) else null

                            withContext(Dispatchers.Main) {
                                postViewModel.createPost(
                                    content = text,
                                    category = selectedCategory, // "Umum"
                                    bitmap = bitmap
                                )
                            }
                        }
                    } else {
                        // MENGGUNAKAN CONTEXT YANG SUDAH DIDEKLARASIKAN
                        Toast.makeText(context, "Isi konten atau foto dulu!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                shape = RoundedCornerShape(12.dp),
                enabled = uploadState !is UploadState.Uploading
            ) {
                if (uploadState is UploadState.Uploading) {
                    CircularProgressIndicator(color = PrimaryDarkNavy, modifier = Modifier.size(24.dp))
                } else {
                    Text("Posting", color = PrimaryDarkNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}