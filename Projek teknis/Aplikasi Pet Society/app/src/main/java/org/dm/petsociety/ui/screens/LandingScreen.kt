package org.dm.petsociety.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dm.petsociety.R
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.ButtonDark

@Composable
fun LandingScreen(onStartClick: () -> Unit) {
    Surface(color = AccentTeal) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Status Bar Placeholder (10:20)
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                // Indikator kecil (Simulasi UI Onboarding)
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Teks Header
            Text(
                text = "Find the best pet in\nyour location",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose a location and our app will show\nyou all the dogs in this area.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.8f))
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Tombol Start
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonDark // Warna tombol gelap
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Anda harus menambahkan file `dog_landing.png` ke `res/drawable`
                //
                Image(
                    painter = painterResource(id = R.drawable.logo_landing),
                    contentDescription = "Landing Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // Gambar Anjing (Asumsi R.drawable.dog_landing ditambahkan)
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Anda harus menambahkan file `dog_landing.png` ke `res/drawable`
                //
                Image(
                    painter = painterResource(id = R.drawable.dog_landing),
                    contentDescription = "Landing Dog",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}