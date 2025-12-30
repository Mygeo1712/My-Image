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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
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
import org.dm.petsociety.model.Role
import org.dm.petsociety.ui.theme.AccentTeal
import org.dm.petsociety.ui.theme.PrimaryDarkNavy
import org.dm.petsociety.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    Surface(color = PrimaryDarkNavy) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Bar Placeholder
            Spacer(modifier = Modifier.height(50.dp))

            // Header
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)) {
                Text(
                    "Let's go!",
                    style = MaterialTheme.typography.headlineLarge.copy(color = AccentTeal),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Fill in your details to get started.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f))
                )
            }

            // --- Form Pendaftaran ---

            // Field Name
            AuthTextField(
                value = state.name,
                onValueChange = { viewModel.setRegisterName(it) },
                placeholder = "Name",
                leadingIcon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field Email
            AuthTextField(
                value = state.email,
                onValueChange = { viewModel.setRegisterEmail(it) },
                placeholder = "Mail",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Mail
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field Password
            AuthTextField(
                value = state.password,
                onValueChange = { viewModel.setRegisterPassword(it) },
                placeholder = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                leadingIcon = Icons.Default.Lock
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field Konfirm Password
            AuthTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.setConfirmPassword(it) },
                placeholder = "Confirm Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                leadingIcon = Icons.Default.Lock
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Select Role (Dropdown)
            RoleDropdown(
                selectedRole = state.role,
                onRoleSelected = { viewModel.setRole(it) }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Register
            AuthButton(
                text = "Register",
                onClick = {
                    viewModel.register { isSuccess, message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        if (isSuccess) {

                            // *** LOGIKA BARU: Ingat akun jika emailnya berdomain Google ***
                            val registeredEmail = viewModel.registerState.value.email
                            if (registeredEmail.endsWith("@gmail.com", ignoreCase = true)) {
                                viewModel.rememberGoogleAccount(registeredEmail)
                            }

                            onRegisterSuccess()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Link Log In
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I already have an account.", color = Color.White.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Log In",
                    color = AccentTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onLoginClick)
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
        }
    }
}

@Composable
fun RoleDropdown(selectedRole: String, onRoleSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedRole.ifEmpty { "Select Role" },
            onValueChange = { },
            readOnly = true,
            label = { Text("Role", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { expanded = true },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, "dropdown",
                    Modifier.clickable { expanded = !expanded })
            },
            colors = AuthTextFieldColors()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Role.options.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}