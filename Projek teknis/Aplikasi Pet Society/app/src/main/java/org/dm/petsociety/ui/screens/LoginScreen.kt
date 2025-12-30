// File: org.dm.petsociety.ui.screens.LoginScreen.kt

package org.dm.petsociety.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dm.petsociety.R
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.loginState.collectAsState()
    val rememberedAccounts by viewModel.availableGoogleAccounts.collectAsState()
    val context = LocalContext.current

    var showAccountSelector by remember { mutableStateOf(false) }

    Surface(color = PrimaryDarkNavy) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Header
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                Text(
                    "Login!",
                    style = MaterialTheme.typography.headlineLarge.copy(color = AccentTeal),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Fill in your details to get started.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f))
                )
            }

            // Field Email
            AuthTextField(
                value = state.email,
                onValueChange = { viewModel.setEmail(it) },
                placeholder = "Mail",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Mail
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field Password
            AuthTextField(
                value = state.password,
                onValueChange = { viewModel.setPassword(it) },
                placeholder = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                leadingIcon = Icons.Default.Lock
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password Button (Text yang bisa diklik)
            Text(
                text = "Have you forgotten your password?",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(onClick = onForgotPasswordClick)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Log In
            AuthButton(
                text = if (state.isLoading) "Logging In..." else "Log In",
                onClick = { viewModel.login(onLoginSuccess) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Continue with Google Button
            AuthButton(
                text = "Continue with Google",
                onClick = { showAccountSelector = true },
                backgroundColor = Color.White,
                contentColor = Color.Black,
                leadingIconRes = R.drawable.ic_google_logo,
                iconColor = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Link Create an account
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I don't have an account.", color = Color.White.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Create an account",
                    color = AccentTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onCreateAccountClick)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(30.dp))

            // Terms of Use dan Privacy Policy
            Text(
                text = buildAnnotatedString {
                    append("By logging in, I agree to the ")
                    withStyle(style = SpanStyle(color = AccentTeal, fontWeight = FontWeight.Bold)) {
                        append("Terms of use")
                    }
                    append(" and ")
                    withStyle(style = SpanStyle(color = AccentTeal, fontWeight = FontWeight.Bold)) {
                        append("privacy policy.")
                    }
                },
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            state.errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.clearLoginError()
            }
        }
    }

    // --- DIALOG PEMILIHAN AKUN ---
    if (showAccountSelector) {
        AlertDialog(
            onDismissRequest = { showAccountSelector = false },
            containerColor = Color.White,
            title = { Text("Pilih Akun Google", color = PrimaryDarkNavy) },
            text = {
                Column {
                    if (rememberedAccounts.isNotEmpty()) {
                        Text(
                            text = "Akun yang Diingat:",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Tampilkan semua akun yang diingat
                        rememberedAccounts.forEach { email ->
                            Text(
                                text = email,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 1. Panggil handleGoogleSignIn (simulasi login)
                                        viewModel.handleGoogleSignIn(
                                            email,
                                            onSuccess = { isRegistered ->
                                                if (isRegistered) {
                                                    Toast.makeText(context, "Akun $email dipilih. Masukkan password dan klik 'Log In'.", Toast.LENGTH_LONG).show()
                                                    viewModel.setEmail(email) // Isi email di field
                                                    viewModel.setPassword("") // Bersihkan password lama
                                                }
                                            },
                                            onError = { message ->
                                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                                onCreateAccountClick() // Pindah ke Register
                                            }
                                        )
                                        showAccountSelector = false
                                    }
                                    .padding(vertical = 12.dp),
                                color = PrimaryDarkNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        Text(
                            "Belum ada akun yang pernah login melalui Google di aplikasi ini. Gunakan Login Manual.",
                            color = PrimaryDarkNavy.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Opsi Tambahan (Gunakan Akun Baru)
                    Text(
                        text = "Gunakan Akun Google Baru / Lain",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "Silakan Login manual, kami akan mengingat akun Google Anda.", Toast.LENGTH_LONG).show()
                                viewModel.setEmail("")
                                viewModel.setPassword("")
                                showAccountSelector = false
                            }
                            .padding(vertical = 12.dp),
                        color = AccentTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAccountSelector = false }) {
                    Text("Batal")
                }
            }
        )
    }
}