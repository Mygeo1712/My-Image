package org.dm.petsociety.ui.screens.petowner.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.dm.petsociety.model.Pet
import org.dm.petsociety.viewmodel.AdoptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionListScreen(
    navController: NavController,
    viewModel: AdoptionViewModel = viewModel()
) {
    val pets by viewModel.adoptionPets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Anjing", "Kucing", "Burung")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temukan Teman Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Background abu muda
        ) {
            // --- HEADER SEARCH & FILTER ---
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(bottom = 16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.filterPets(searchQuery, selectedType)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Cari ras atau nama...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips (Scrollable Row could be added here, simplified for now)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                viewModel.filterPets(searchQuery, selectedType)
                            },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFB74D), // Orange PetSociety
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // --- LIST CONTENT ---
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (pets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Text("Tidak ada hewan ditemukan", color = Color.Gray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pets) { pet ->
                        AdoptionPetCard(pet = pet) {
                            // Navigasi ke Detail (Next Step)
                            navController.navigate("adoption_detail/${pet.petId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdoptionPetCard(pet: Pet, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // Gambar Full Width
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                AsyncImage(
                    model = pet.profileImageUrl ?: "https://via.placeholder.com/300",
                    contentDescription = pet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Badge Gender (Pojok Kanan Atas)
                Surface(
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    color = if (pet.gender == "Jantan") Color(0xFFE3F2FD) else Color(0xFFFCE4EC),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (pet.gender == "Jantan") "♂" else "♀",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (pet.gender == "Jantan") Color.Blue else Color.Magenta,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info text
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pet.breed,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${pet.age} Thn",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4CAF50) // Green
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Tombol kecil "Lihat"
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}