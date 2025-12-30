package org.dm.petsociety.ui.screens.petowner.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import org.dm.petsociety.R
import org.dm.petsociety.ui.screens.AppBottomBar
import org.dm.petsociety.ui.screens.AppTopBar
import org.dm.petsociety.ui.theme.* import androidx.compose.ui.platform.LocalContext

@Composable
fun PetOwnerSearchScreen(navController: NavController) {
    Scaffold(
        topBar = { AppTopBar(title = "Cari", navController = navController, showProfile = true) },
        containerColor = PrimaryDarkNavy,
        bottomBar = { AppBottomBar(navController = navController, currentRoute = "home_owner_search") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryDarkNavy)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                "Temukan Yang Terbaik Untuk Hewan Anda",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // --- Menu Pilihan ---

            // 1. Playdate
            SearchMenuCard(
                title = "PLAYDATE",
                iconResId = R.drawable.playdate,
                bgColor = CardPlaydate,
                onClick = { navController.navigate("home_owner_playdate") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Adopsi
            SearchMenuCard(
                title = "ADOPSI",
                iconResId = R.drawable.adopsi,
                bgColor = CardAdoption,
                imageScale = 0.8f,
                onClick = { navController.navigate("adoption_list") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Layanan Profesional (Expert) - UPDATE DISINI
            SearchMenuCard(
                title = "LAYANAN PROFESIONAL", // Judul Baru
                iconResId = R.drawable.dokter_hewan,
                bgColor = CardExpert,
                onClick = {
                    // Arahkan ke rute list khusus Pet Owner
                    navController.navigate("expert_list_petowner")
                }
            )
        }
    }
}

@Composable
fun SearchMenuCard(
    title: String,
    iconResId: Int,
    bgColor: Color,
    onClick: () -> Unit,
    imageScale: Float = 1f // default normal
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Kiri: Gambar
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().scale(imageScale)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Kanan: Teks dan Tombol
            Column(
                modifier = Modifier.weight(0.6f).fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                )

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cari", color = AccentTeal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}