// File: org.dm.petsociety.ui.screens.ForgotPasswordScreen.kt

package org.dm.petsociety.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.viewmodel.AuthViewModel

import org.dm.petsociety.viewmodel.ForgotPasswordState

@Composable
fun ForgotPasswordInputScreen(
    viewModel: AuthViewModel,
    onSendSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val state: ForgotPasswordState by viewModel.forgotPasswordState.collectAsState()

    Surface(color = PrimaryDarkNavy, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)) {
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

            // Dialog Forgot Password
            AlertDialog(
                onDismissRequest = { /* Tidak ada dismiss agar tetap terlihat seperti overlay */ },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp),
                containerColor = Color.White,

                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Have you forgotten your password?",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .clickable { onDismiss() }
                                .padding(top = 4.dp)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Don't worry, write the email with which you are registered and we will send you the steps you must follow to change your password.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AuthTextField(
                            value = state.email,
                            onValueChange = { viewModel.setForgotPasswordEmail(it) },
                            placeholder = "Mail",
                            keyboardType = KeyboardType.Email,
                            colors = AuthTextFieldColors().copy(
                                unfocusedContainerColor = Color(0xFFF0F0F0),
                                focusedContainerColor = Color(0xFFF0F0F0),
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black
                            )
                        )
                    }
                },
                confirmButton = {
                    AuthButton(
                        text = if (state.isLoading) "Sending..." else "Send",
                        onClick = {
                            if (!state.isLoading) {
                                viewModel.sendResetPasswordEmail(
                                    onSuccess = {
                                        Toast.makeText(context, "Email reset password berhasil dikirim!", Toast.LENGTH_LONG).show()
                                        onSendSuccess()
                                    },
                                    onError = { message: String ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = AccentTeal
                    )
                }
            )
        }
    }
}

@Composable
fun ForgotPasswordConfirmationScreen(onAcceptClick: () -> Unit) {
    Surface(color = PrimaryDarkNavy, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)) {
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

            AlertDialog(
                onDismissRequest = { /* Tidak ada dismiss */ },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp),
                containerColor = Color.White,

                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Have you forgotten your password?",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .clickable { onAcceptClick() }
                                .padding(top = 4.dp)
                        )
                    }
                },

                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "We have sent you an email with the steps you must follow to change your password.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ICON: EMAIL SENT", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                },
                confirmButton = {
                    AuthButton(
                        text = "Accept",
                        onClick = onAcceptClick,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = AccentTeal
                    )
                }
            )
        }
    }
}