package org.dm.petsociety.ui.screens.petowner.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import org.dm.petsociety.viewmodel.ExpertViewModel
import org.dm.petsociety.viewmodel.ServiceWithExpert

// Gunakan Warna Tema Gelap agar konsisten
val DarkBg = Color(0xFF141927)
val CardBg = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertListScreen(
    navController: NavController,
    viewModel: ExpertViewModel
) {
    val feed by viewModel.expertFeed.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Ambil data saat layar dibuka (ViewModel ini sudah memfilter status "Available")
    LaunchedEffect(Unit) {
        viewModel.fetchExpertFeed()
    }

    // Filter lokal untuk Search Bar
    val filteredFeed = feed.filter {
        it.service.serviceName.contains(searchQuery, ignoreCase = true) ||
                it.service.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Layanan Profesional", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                )
            )
        },
        containerColor = DarkBg
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari layanan (Vaksin, Grooming...)", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredFeed.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada layanan yang ditemukan.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredFeed) { item ->
                        PetOwnerServiceCard(
                            item = item,
                            onClick = {
                                // Ke halaman detail (reuse halaman detail expert yg sudah ada)
                                navController.navigate("expert_service_detail/${item.service.serviceId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetOwnerServiceCard(
    item: ServiceWithExpert,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column {
            // Gambar
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                AsyncImage(
                    model = item.service.imageUrl.ifEmpty { "https://via.placeholder.com/200" },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Badge Kategori
                Surface(
                    color = when(item.service.category) {
                        "Medis" -> Color(0xFF4CAF50)
                        "Grooming" -> Color(0xFFE91E63)
                        else -> Color(0xFF2196F3)
                    },
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = RoundedCornerShape(bottomStart = 8.dp)
                ) {
                    Text(
                        text = item.service.category,
                        color = Color.White,
                        modifier = Modifier.padding(6.dp, 2.dp),
                        fontSize = 10.sp
                    )
                }
            }

            // Info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.service.serviceName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = item.service.price,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Expert Name
                Text(
                    text = "Oleh: ${item.expertName}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                // Lokasi (jika ada di model ExpertService, tapi di visualisasi UI ini opsional)
                // Jika ingin menampilkan lokasi, pastikan field location ada di ServiceWithExpert atau ExpertService
            }
        }
    }
}