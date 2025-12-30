// File: org.dm.petsociety.ui.screens.expert.ExpertHomeScreen.kt

package org.dm.petsociety.ui.screens.expert

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.dm.petsociety.viewmodel.ExpertViewModel
import org.dm.petsociety.viewmodel.ServiceWithExpert
import org.dm.petsociety.ui.screens.common.ChatListScreen
import org.dm.petsociety.viewmodel.AuthViewModel
import androidx.compose.material3.FabPosition


// --- KONSTANTA WARNA TEMA ---
val DarkBg = Color(0xFF141927)
val CardBg = Color(0xFF1F2937)
val AccentGreen = Color(0xFF4CAF50)

@Composable
fun ExpertHomeScreen(navController: NavController, viewModel: ExpertViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val showFab = selectedTab == 0 || selectedTab == 2

    val authViewModel: AuthViewModel = viewModel()

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White, contentColor = AccentGreen) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) }, label = { Text("Beranda") },
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chat") },
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profil") },
                    selected = selectedTab == 2, onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
                )
            }
        },
        floatingActionButton = {
            if (showFab && selectedTab == 0) {
                FloatingActionButton(
                    onClick = { navController.navigate("expert_add_service") },
                    containerColor = AccentGreen, contentColor = Color.White
                ) { Icon(Icons.Default.Add, null) }
            } else if (showFab && selectedTab == 2) {
                FloatingActionButton(
                    onClick = { navController.navigate("expert_edit_profile") },
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ExpertFeedContent(navController, viewModel)
                1 -> {
                    ChatListScreen(
                        navController = navController,
                        viewModel = viewModel(),
                        role = "Expert"
                    )
                }
                2 -> ExpertProfileContent(navController, viewModel, authViewModel)
            }
        }
    }
}

@Composable
fun ExpertFeedContent(navController: NavController, viewModel: ExpertViewModel) {
    val feed by viewModel.expertFeed.collectAsState()
    val profile by viewModel.currentUserProfile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExpertFeed()
        viewModel.fetchExpertProfile()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = profile.profileImageUrl.ifEmpty { "https://via.placeholder.com/150" },
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Halo, ${profile.name}! 👨‍⚕️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Kelola layanan expert Anda", color = Color.Gray, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Layanan Aktif Komunitas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (feed.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada layanan aktif.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feed) { item ->
                    ServiceCard(
                        item = item,
                        onClick = { navController.navigate("expert_service_detail/${item.service.serviceId}") }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpertProfileContent(
    navController: NavController,
    viewModel: ExpertViewModel,
    authViewModel: AuthViewModel
) {
    val myServices by viewModel.myServices.collectAsState()
    val profile by viewModel.currentUserProfile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyServices()
        viewModel.fetchExpertProfile()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = profile.profileImageUrl.ifEmpty { "https://via.placeholder.com/150" },
                contentDescription = null,
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(profile.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)

            if (profile.specialization.isNotEmpty()) {
                Text(profile.specialization, color = AccentGreen, fontSize = 14.sp)
            } else {
                Text("Expert Partner", color = AccentGreen)
            }

            if (profile.location.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(profile.location, color = Color.Gray, fontSize = 12.sp)
                }
            }

            if (profile.bio.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile.bio,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Button(
                onClick = { navController.navigate("expert_edit_profile") },
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) { Text("Edit Profil") }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("landing") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red.copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.8f))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Layanan Saya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (myServices.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Belum ada layanan yang dibuat.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(min = 10.dp, max = 800.dp)
            ) {
                items(myServices) { service ->
                    val item = ServiceWithExpert(service, profile.name, profile.profileImageUrl)
                    ServiceCard(
                        item = item,
                        onClick = { navController.navigate("expert_service_detail/${service.serviceId}") },
                        showDelete = true,
                        onDelete = { viewModel.deleteService(service.serviceId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    item: ServiceWithExpert,
    onClick: () -> Unit,
    showDelete: Boolean = false,
    onDelete: () -> Unit = {}
) {
    val isAvailable = item.service.status == "Available"
    val statusColor = if (isAvailable) Color.Green else Color.Red
    val statusText = if (isAvailable) "Tersedia" else "Tdk Tersedia"

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                AsyncImage(
                    model = item.service.imageUrl.ifEmpty { "https://via.placeholder.com/200" },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
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

            Column(modifier = Modifier.padding(10.dp)) {
                Text(item.service.serviceName, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(item.service.price, color = AccentGreen, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(8.dp), onDraw = { drawCircle(statusColor) })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(statusText, color = statusColor, fontSize = 10.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (showDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}